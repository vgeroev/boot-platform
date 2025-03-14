package org.vmalibu.module.core.service.tag;

import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.core.database.dao.TagDAO;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.core.service.tag.event.BeforeTagsRemoveEvent;
import org.vmalibu.module.core.service.tag.list.TagPagingRequest;
import org.vmalibu.module.core.utils.HexColorUtils;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.vmalibu.modules.utils.database.DatabaseFunctionNames.PG_TRGM_CONTAINED_BY;

@Service
public class TagServiceImpl implements TagService {

    private final TagDAO tagDAO;
    private final DomainObjectPagination<DBTag, TagDTO> domainObjectPagination;
    private final ApplicationEventPublisher eventPublisher;

    public TagServiceImpl(TagDAO tagDAO, ApplicationEventPublisher eventPublisher) {
        this.tagDAO = tagDAO;
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(tagDAO, TagDTO::from);
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable TagDTO findById(long id) {
        return tagDAO.findById(id).map(TagDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull PaginatedDto<TagDTO> findAll(@NonNull TagPagingRequest request) {
        return domainObjectPagination.findAll(
                request,
                buildSpecification(
                        request.getSearchText(),
                        request.getFilterIds()
                )
        );
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull TagDTO create(@NonNull String name, int color) throws PlatformException {
        if (!StringUtils.hasText(name)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBTag.class, DBTag.Fields.name);
        }

        checkColor(color);
        checkNameUniqueness(name);

        DBTag tag = new DBTag();
        tag.setName(name);
        tag.setColor(color);

        return TagDTO.from(tagDAO.save(tag));
    }

    @Override
    @Transactional
    public void remove(@NonNull Set<@NonNull Long> ids) {
        eventPublisher.publishEvent(new BeforeTagsRemoveEvent(ids));
        tagDAO.deleteAllById(ids);
    }

    private void checkColor(int color) throws PlatformException {
        if (!HexColorUtils.isInColorRange(color)) {
            throw GeneralExceptionFactory.buildInvalidValueException(DBTag.Fields.color);
        }
    }

    private void checkNameUniqueness(String name) throws PlatformException {
        boolean exists = tagDAO.isExistByName(name);
        if (exists) {
            throw GeneralExceptionFactory.buildNotUniqueDomainObjectException(DBTag.class, DBTag.Fields.name, name);
        }
    }

    private static Specification<DBTag> buildSpecification(String searchText, Set<Long> filterIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchText != null) {
                predicates.add(getSearchTextPredicate(root, cb, searchText));
            }

            if (filterIds != null) {
                predicates.add(getFilterIdsPredicate(root, filterIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getSearchTextPredicate(
            Root<DBTag> root,
            CriteriaBuilder cb,
            String value
    ) {
        Path<String> name = root.get(DBTag.DB_NAME);
        Expression<Boolean> trgm = cb.function(PG_TRGM_CONTAINED_BY, Boolean.class, cb.literal(value), name);
        return cb.equal(trgm, true);
    }

    private static Predicate getFilterIdsPredicate(
            Root<DBTag> root,
            Set<Long> filterIds
    ) {
        return root.get("id").in(filterIds);
    }

}
