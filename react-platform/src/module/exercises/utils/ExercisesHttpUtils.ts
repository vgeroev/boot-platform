import { HttpRequest } from "../../../utils/HttpUtils";
import { getAuthorizedUrl } from "../../../utils/UrlUtils";

const EXERCISES_AUTHORIZED_URL = getAuthorizedUrl("exercises", "v1");

//---------------------------------------------------------------------------

const EXCERCISE_SOURCE_LIST_HTTP_REQUEST: HttpRequest = {
  method: "get",
  url: EXERCISES_AUTHORIZED_URL + "/exercise-source/list",
};

const EXCERCISE_SOURCE_CREATE_HTTP_REQUEST: HttpRequest = {
  method: "post",
  url: EXERCISES_AUTHORIZED_URL + "/exercise-source",
};

//---------------------------------------------------------------------------

function getExerciseListHttpRequest(exerciseSourceId: number): HttpRequest {
  return {
    url: EXERCISES_AUTHORIZED_URL + `/${exerciseSourceId}/exercise/list`,
    method: "get",
  };
}

function getExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "get",
    url: EXERCISES_AUTHORIZED_URL + `/exercise/${id}`,
  };
}

function updateExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "patch",
    url: EXERCISES_AUTHORIZED_URL + `/exercise/${id}`,
  };
}
function createExerciseHttpRequest(id: number): HttpRequest {
  return {
    method: "post",
    url: EXERCISES_AUTHORIZED_URL + `/${id}/exercise`,
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
