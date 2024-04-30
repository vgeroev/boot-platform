import { HttpRequest } from "../../../utils/HttpUtils";
import { getAuthorizedUrl } from "../../../utils/UrlUtils";

const EXERCISES_AUTHORIZED_URL = getAuthorizedUrl("exercises", "v1");

function getExercisesAuthorizedUrl(path: string) {
  return EXERCISES_AUTHORIZED_URL + path;
}

//---------------------------------------------------------------------------

const EXCERCISE_SOURCE_LIST_HTTP_REQUEST: HttpRequest = {
  method: "get",
  url: getExercisesAuthorizedUrl("/exercise-source/list"),
};

const EXCERCISE_SOURCE_CREATE_HTTP_REQUEST: HttpRequest = {
  method: "post",
  url: getExercisesAuthorizedUrl("/exercise-source"),
};

//---------------------------------------------------------------------------

function getExerciseListHttpRequest(exerciseSourceId: number): HttpRequest {
  return {
    url: getExercisesAuthorizedUrl(`/${exerciseSourceId}/exercise/list`),
    method: "get",
  };
}

function getExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "get",
    url: getExercisesAuthorizedUrl(`/exercise/${id}`),
  };
}

function updateExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "patch",
    url: getExercisesAuthorizedUrl(`/exercise/${id}`),
  };
}
function createExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "post",
    url: getExercisesAuthorizedUrl(`/${id}/exercise`),
  };
}

export {
  EXCERCISE_SOURCE_LIST_HTTP_REQUEST,
  EXCERCISE_SOURCE_CREATE_HTTP_REQUEST,
  getExerciseListHttpRequest,
  getExerciseHttpRequest,
  updateExerciseHttpRequest,
  createExerciseHttpRequest,
};
