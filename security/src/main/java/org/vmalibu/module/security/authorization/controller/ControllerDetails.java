package org.vmalibu.module.security.authorization.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ControllerDetails {

    private final ControllerAuthDetails authDetails;

}
