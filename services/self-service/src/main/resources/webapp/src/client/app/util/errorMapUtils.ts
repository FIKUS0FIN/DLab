/***************************************************************************

Copyright © 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

export class ErrorMapUtils {
  public static setErrorMessage(errorCode): string {
    if(errorCode)
      var defaultStatus = 'Error status [' + errorCode.status + ']. ';
      var errorMessage = defaultStatus.concat(errorCode.statusText);

      var statusErrorMap = {
        '400': 'Server understood the request, but request content was invalid.',
        '401': 'Unauthorized access.',
        '403': 'Forbidden resource can`t be accessed.',
        '500': 'Internal server error.',
        '503': 'Service unavailable.'
      };

      if(statusErrorMap[errorCode.status])
        errorMessage = defaultStatus.concat(statusErrorMap[errorCode.status]);

      return errorMessage;
  }
}