@echo on
@set SITE=http://localhost:8280
@set CURL=curl -g -i -H "Accept: application/json" -H "Content-Type: application/json"
@set HR_YELLOW=@powershell -Command Write-Host "----------------------------------------------------------------------" -foreground "Yellow"
@set HR_RED=@powershell    -Command Write-Host "----------------------------------------------------------------------" -foreground "Red"


%HR_YELLOW%
@powershell -Command Write-Host "Load sample dataset" -foreground "Green"
%CURL% "%SITE%/loadSampleDataset"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET all departments" -foreground "Green"
%CURL% "%SITE%/company/departments"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET department by department key" -foreground "Green"
%CURL% "%SITE%/company/departments/K-DEP-1"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET employee by department key and names" -foreground "Green"
%CURL% "%SITE%/company/departments/K-DEP-1/employees?firstName=EF-Name-101&lastName=EL-Name-101"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET all teams" -foreground "Green"
%CURL% "%SITE%/company/teams"
@echo.
	
%HR_YELLOW%
@powershell -Command Write-Host "GET teams by ranges" -foreground "Green"
%CURL% "%SITE%/company/teams/range?rangeFrom=1&rangeTo=3"
@echo.
	
%HR_YELLOW%
@powershell -Command Write-Host "Get team rank by team id" -foreground "Green"
%CURL% "%SITE%/company/teams/rank?id=1"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET unknown department. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/company/departments/K-DEP-12345"

%HR_YELLOW%
@powershell -Command Write-Host "GET employee from unknown department. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/company/departments/K-DEP-12345/employees?firstName=EF-Name-101&lastName=EL-Name-101"

%HR_YELLOW%
@powershell -Command Write-Host "GET unknown employee. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/company/departments/K-DEP-1/employees?firstName=unknown&lastName=unknown"

%HR_YELLOW%
@powershell -Command Write-Host "GET teams with unknown ranges. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/company/teams/range?rangeFrom=123&rangeTo=456"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "GET teams with bad range. Receiving error response [400 Bad Request]" -foreground "Magenta"
%CURL% "%SITE%/company/teams/range?rangeFrom=1&rangeTo=ABC"

%HR_YELLOW%
@powershell -Command Write-Host "Get team rank of unknown team. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/company/teams/rank?id=123"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Get team rank with bad team id. Receiving error response [400 Bad Request]" -foreground "Magenta"
%CURL% "%SITE%/company/teams/rank?id=ABC"
@echo.

%HR_RED%
@powershell -Command Write-Host "FINISH" -foreground "Red"
pause
