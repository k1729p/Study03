<!DOCTYPE html>
<html lang="en">
<meta charset="UTF-8">
<body>
<a href="https://github.com/k1729p/Study03/tree/main/docs"><img alt="" src="images/ColorScheme.png" height="25" width="800"/></a>
<h2 id="contents">Study03 README Contents</h2>

<p>
Topics: SpringBoot ● Spring WebFlux ● Reactive Streams ● Reactive REST ● Redis ● Docker ● WebTestClient
</p>

<h3 id="top">Research the Reactive REST Web Service</h3>
<p>
On the server-side, <b>WebFlux</b> supports two distinct programming models:
</p>
<ul>
<li><i>Functional Endpoints</i></li>
<li><i>Annotated Controllers</i></li>
</ul>
<p>
The project <b>Study03</b> implements one of them, the "<i>Functional Endpoints</i>" programming model.
</p>

<p><img alt="" src="images/MermaidFlowchart.png" height="350" width="675"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The flowchart with Docker containers '<b>study03</b>' and '<b>redis</b>'.</i>
</p>

<p>
<b>Redis</b> is an in-memory data structure store.<br>
The <a href="images/ScreenshotsRedisInsight.png">screenshots</a> from the
<a href="https://redis.com/redis-enterprise/redis-insight/">RedisInsight</a> (Redis data visualizer and optimizer).
</p>

<p>
The sections of this project:
</p>
<ol>
<li><a href="#ONE"><b>Docker Build</b></a></li>
<li><a href="#TWO"><b>Web Browser Client</b></a></li>
<li><a href="#THREE"><b>Java Client</b></a></li>
<li><a href="#FOUR"><b>Curl Client</b></a></li>
</ol>

<hr>

<p>
Java source code. Packages:<br>
<img alt="" src="images/aquaHR-500.png"><br>
<img alt="" src="images/aquaSquare.png">
    <i>application sources</i>&nbsp;:&nbsp;
	<a href="https://github.com/k1729p/Study03/tree/main/src/main/java/kp">kp</a><br>
<img alt="" src="images/aquaSquare.png">
    <i>test sources</i>&nbsp;:&nbsp;
	<a href="https://github.com/k1729p/Study03/tree/main/src/test/java/kp">kp</a><br>
<img alt="" src="images/aquaHR-500.png">
</p>

<br>

<p><img alt="" src="images/MermaidClassDiagram.png" height="440" width="590"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The domain objects class diagram.</i>
</p>

<p>
<img alt="" src="images/yellowHR-500.png"><br>
<img alt="" src="images/yellowSquare.png">
<a href="http://htmlpreview.github.io/?https://github.com/k1729p/Study03/blob/main/docs/apidocs/index.html">
Java API Documentation</a>&nbsp;●&nbsp;
<a href="http://htmlpreview.github.io/?https://github.com/k1729p/Study03/blob/main/docs/testapidocs/index.html">
Java Test API Documentation</a><br>
<img alt="" src="images/yellowHR-500.png">
</p>
<hr>
<h3 id="ONE">❶ Docker Build</h3>

<p>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/01%20Docker%20build%20and%20run.bat">
 <i>"01 Docker build and run.bat"</i></a> build the image and<br>
<img alt="" src="images/orangeSquare.png"><img alt="" src="images/spacer-32.png">start the container with the SpringBoot server.<br>
<img alt="" src="images/orangeHR-500.png">
</p>

<p><img alt="" src="images/greenCircle.png">
1.1. Docker image is built using these files:
<a href="https://raw.githubusercontent.com/k1729p/Study03/main/docker-config/Dockerfile"><b>Dockerfile</b></a> and
<a href="https://raw.githubusercontent.com/k1729p/Study03/main/docker-config/compose.yaml"><b>compose.yaml</b></a>.
</p>
<p>
<img alt="" src="images/ScreenshotDockerContainer.png" height="365" width="950"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The screenshot of the created Docker containers.</i>
</p>

<a href="#top">Back to the top of the page</a>
<hr>
<h3 id="TWO">❷ Web Browser Client</h3>

<p>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With the URL <a href="http://localhost:8280">http://localhost:8280</a> open in the web browser the 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">home page</a>.<br>
<img alt="" src="images/orangeSquare.png"> 2. On this
<a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">home page</a>
select 'Load sample dataset' <a href="http://localhost:8280/loadSampleDataset">http://localhost:8280/loadSampleDataset</a>.<br>
<img alt="" src="images/orangeHR-500.png">
</p>

<p><img alt="" src="images/greenCircle.png">
2.1. The <a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">
home page</a> on the Docker link: <a href="http://localhost:8280/"><b>http://localhost:8280</b></a>.<br>

<img alt="" src="images/ScreenshotHomePage.png" height="315" width="985"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The screenshot of the home page.</i>
</p>

<p>
Below are the results from some selected links on the home page.
</p>

<p><img alt="" src="images/greenCircle.png">
2.2. The 'Get all <b>departments</b>' link: 
<a href="http://localhost:8280/company/departments">
http://localhost:8280/company/departments</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/DepartmentHandler.java#L50">
kp.company.handlers.DepartmentHandler::handleDepartments</a>.<br>
</p>
<p><img alt="" src="images/GetAllDepartments.png" height="480" width="300"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get all <b>departments</b>'.</i>
</p>

<p><img alt="" src="images/greenCircle.png">
2.3. The 'Get the <b>department</b> by department key' link: 
<a href="http://localhost:8280/company/departments/K-DEP-1">
http://localhost:8280/company/departments/K-DEP-1</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/DepartmentHandler.java#L72">
kp.company.handlers.DepartmentHandler::handleDepartmentByDepartmentKey</a>.<br>
</p>
<p><img alt="" src="images/GetDepartmentByDepartmentKey.png" height="235" width="275"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get the <b>department</b> by department key'.</i>
</p>

<p><img alt="" src="images/greenCircle.png">
2.4. The 'Get the <b>employee</b> by department key and employee names' link: 
<a href="http://localhost:8280/company/departments/K-DEP-1/employees/?firstName=EF-Name-101&lastName=EL-Name-101">
http://localhost:8280/company/departments/K-DEP-1/employees/?firstName=EF-Name-101&lastName=EL-Name-101</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/EmployeeHandler.java#L51">
kp.company.handlers.EmployeeHandler::handleEmployeeByDepartmentKeyAndNames</a>.<br>
</p>
<p><img alt="" src="images/GetEmployeeByDepartmentKeyAndEmployeeNames.png" height="80" width="220"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get the <b>employee</b> by department key and employee names'.</i>
</p>

<p><img alt="" src="images/greenCircle.png">
2.5. The 'Get all <b>teams</b> and scores' link: 
<a href="http://localhost:8280/company/teams">
http://localhost:8280/company/teams</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L52">
kp.company.handlers.TeamHandler::handleTeams</a>.<br>
</p>
<p><img alt="" src="images/GetAllTeamsAndScores.png" height="545" width="130"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get all <b>teams</b> and scores'.</i>
</p>

<p><img alt="" src="images/greenCircle.png">
2.6. The 'Get the <b>teams</b> and scores by ranges' link: 
<a href="http://localhost:8280/company/teams/range?rangeFrom=1&rangeTo=3">
http://localhost:8280/company/teams/range?rangeFrom=1&rangeTo=3</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L78">
kp.company.handlers.TeamHandler::handleTeamsRangeByScore</a>.<br>
</p>
<p><img alt="" src="images/GetTeamsAndScoresByRanges.png" height="345" width="135"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get the <b>teams</b> and scores by ranges'.</i>
</p>

<p><img alt="" src="images/greenCircle.png">
2.7. The 'Get the <b>team</b> rank by team id' link: 
<a href="http://localhost:8280/company/teams/rank?id=1">
http://localhost:8280/company/teams/rank?id=1</a>.
</p>
<p>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L115">
kp.company.handlers.TeamHandler::handleTeamRankById</a>.<br>
</p>
<p><img alt="" src="images/GetTeamRankByTeamId.png" height="30" width="30"/><br>
<img alt="" src="images/blackArrowUp.png">
<i>The result from the 'Get the <b>team</b> rank by team id'.</i>
</p>

<a href="#top">Back to the top of the page</a>
<hr>
<h3 id="THREE">❸ Java Client</h3>

<p>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/03%20MVN%20clean%20install%20execute%20client.bat">
 <i>"03 MVN clean install execute client.bat"</i></a> launch the Java client.<br>
<img alt="" src="images/orangeHR-500.png">
</p>

<p><img alt="" src="images/greenCircle.png">
3.1. The client application <a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/WebClientLauncher.java">
kp.client.WebClientLauncher</a>.<br>
The method 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/WebClientLauncher.java#L57">
kp.client.WebClientLauncher::performRequests</a> starts three subscribers:<br>
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/DepartmentSubscriber.java">DepartmentSubscriber</a>, 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/EmployeeSubscriber.java">EmployeeSubscriber</a>, 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/TeamSubscriber.java">TeamSubscriber</a>.
</p>
<p>
The <a href="images/ScreenshotJavaClient.png">
<b>screenshot</b></a>
of the console log from the run of the batch file <b>"03 MVN clean install execute client.bat"</b>
</p>

<a href="#top">Back to the top of the page</a>
<hr>
<h3 id="FOUR">❹ Curl Client</h3>

<p>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/04%20CURL%20call%20server.bat">
 <i>"04 CURL call server.bat"</i></a> load the sample dataset and get departments and employees.<br>
<img alt="" src="images/orangeHR-500.png">
</p>

<p><img alt="" src="images/greenCircle.png">
4.1. The <a href="images/ScreenshotCurlCallServer.png">
<b>screenshot</b></a>
of the console log from the run of the batch file <b>"04 CURL call server.bat"</b>
</p>

<a href="#top">Back to the top of the page</a>
<hr>
</body>
</html>