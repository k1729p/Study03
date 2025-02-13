<!DOCTYPE html>
<HTML lang="en">
<META charset="UTF-8">
<BODY>
<a href="https://github.com/k1729p/Study03/tree/main/docs"><img alt="" src="images/ColorScheme.png" height="25" width="800"/></a>
<H2 id="contents">Study03 README Contents</H2>

<P>
Topics: SpringBoot ● Spring WebFlux ● Reactive Streams ● Reactive REST ● Redis ● Docker ● WebTestClient
</P>

<H3 id="top">Research the Reactive REST Web Service</H3>
<P>
On the server-side, <b>WebFlux</b> supports two distinct programming models:
</P>
<UL>
<LI><i>Functional Endpoints</i></LI>
<LI><i>Annotated Controllers</i></LI>
</UL>
<P>
The project <b>Study03</b> implements one of them, the "<i>Functional Endpoints</i>" programming model.
</P>

<P><img alt="" src="images/MermaidFlowchart.png" height="350" width="675"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The flowchart with Docker containers '<b>study03</b>' and '<b>redis</b>'.</I></P>

<P>
<b>Redis</b> is an in-memory data structure store.<br>
The <a href="images/ScreenshotsRedisInsight.png">screenshots</a> from the
<a href="https://redis.com/redis-enterprise/redis-insight/">RedisInsight</a> (Redis data visualizer and optimizer).
</P>

<P>
The sections of this project:
</P>
<OL>
<LI><a href="#ONE"><b>Docker Build</b></a></LI>
<LI><a href="#TWO"><b>Web Browser Client</b></a></LI>
<LI><a href="#THREE"><b>Java Client</b></a></LI>
<LI><a href="#FOUR"><b>Curl Client</b></a></LI>
</OL>

<hr>

<P>
Java source code. Packages:<br>
<img alt="" src="images/aquaHR-500.png"><br>
<img alt="" src="images/aquaSquare.png">
    <i>application sources</i>&nbsp;:&nbsp;
	<a href="https://github.com/k1729p/Study03/tree/main/src/main/java/kp">kp</a><br>
<img alt="" src="images/aquaSquare.png">
    <i>test sources</i>&nbsp;:&nbsp;
	<a href="https://github.com/k1729p/Study03/tree/main/src/test/java/kp">kp</a><br>
<img alt="" src="images/aquaHR-500.png">
</P>

<br>

<P><img alt="" src="images/MermaidClassDiagram.png" height="440" width="590"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The domain objects class diagram.</I></P>

<P>
<img alt="" src="images/yellowHR-500.png"><br>
<img alt="" src="images/yellowSquare.png">
<a href="http://htmlpreview.github.io/?https://github.com/k1729p/Study03/blob/main/docs/apidocs/index.html">
Java API Documentation</a>&nbsp;●&nbsp;
<a href="http://htmlpreview.github.io/?https://github.com/k1729p/Study03/blob/main/docs/testapidocs/index.html">
Java Test API Documentation</a><br>
<img alt="" src="images/yellowHR-500.png">
</P>
<hr>
<H3 id="ONE">❶ Docker Build</H3>

<P>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/01%20Docker%20build%20and%20run.bat">
 <I>"01 Docker build and run.bat"</I></a> build the image and<br>
<img alt="" src="images/orangeSquare.png"><img alt="" src="images/spacer-32.png">start the container with the SpringBoot server.<br>
<img alt="" src="images/orangeHR-500.png"></P>

<P><img alt="" src="images/greenCircle.png">
1.1. Docker image is built using these files:
<a href="https://raw.githubusercontent.com/k1729p/Study03/main/docker-config/Dockerfile"><b>Dockerfile</b></a> and
<a href="https://raw.githubusercontent.com/k1729p/Study03/main/docker-config/compose.yaml"><b>compose.yaml</b></a>.
</P>
<P>
<img alt="" src="images/ScreenshotDockerContainer.png" height="365" width="950"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The screenshot of the created Docker containers.</I>
</P>

<a href="#top">Back to the top of the page</a>
<hr>
<H3 id="TWO">❷ Web Browser Client</H3>

<P>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With the URL <a href="http://localhost:8280">http://localhost:8280</a> open in the web browser the 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">home page</a>.<br>
<img alt="" src="images/orangeSquare.png"> 2. On this
<a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">home page</a>
select 'Load sample dataset' <a href="http://localhost:8280/loadSampleDataset">http://localhost:8280/loadSampleDataset</a>.<br>
<img alt="" src="images/orangeHR-500.png"></P>

<P><img alt="" src="images/greenCircle.png">
2.1. The <a href="https://github.com/k1729p/Study03/blob/main/src/main/resources/static/index.html">
home page</a> on the Docker link: <a href="http://localhost:8280/"><b>http://localhost:8280</b></a>.<br>

<img alt="" src="images/ScreenshotHomePage.png" height="315" width="985"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The screenshot of the home page.</I>
</P>

<P>
Below are the results from some selected links on the home page.
</P>

<P><img alt="" src="images/greenCircle.png">
2.2. The 'Get all <b>departments</b>' link: 
<a href="http://localhost:8280/company/departments">
http://localhost:8280/company/departments</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/DepartmentHandler.java#L49">
kp.company.handlers.DepartmentHandler::handleDepartments</a>.<br>
</P>
<P><img alt="" src="images/GetAllDepartments.png" height="480" width="300"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get all <b>departments</b>'.</I></P>

<P><img alt="" src="images/greenCircle.png">
2.3. The 'Get the <b>department</b> by department key' link: 
<a href="http://localhost:8280/company/departments/K-DEP-1">
http://localhost:8280/company/departments/K-DEP-1</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/DepartmentHandler.java#L71">
kp.company.handlers.DepartmentHandler::handleDepartmentByDepartmentKey</a>.<br>
</P>
<P><img alt="" src="images/GetDepartmentByDepartmentKey.png" height="235" width="275"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get the <b>department</b> by department key'.</I></P>

<P><img alt="" src="images/greenCircle.png">
2.4. The 'Get the <b>employee</b> by department key and employee names' link: 
<a href="http://localhost:8280/company/departments/K-DEP-1/employees/?firstName=EF-Name-101&lastName=EL-Name-101">
http://localhost:8280/company/departments/K-DEP-1/employees/?firstName=EF-Name-101&lastName=EL-Name-101</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/EmployeeHandler.java#L50">
kp.company.handlers.EmployeeHandler::handleEmployeeByDepartmentKeyAndNames</a>.<br>
</P>
<P><img alt="" src="images/GetEmployeeByDepartmentKeyAndEmployeeNames.png" height="80" width="220"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get the <b>employee</b> by department key and employee names'.</I></P>

<P><img alt="" src="images/greenCircle.png">
2.5. The 'Get all <b>teams</b> and scores' link: 
<a href="http://localhost:8280/company/teams">
http://localhost:8280/company/teams</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L52">
kp.company.handlers.TeamHandler::handleTeams</a>.<br>
</P>
<P><img alt="" src="images/GetAllTeamsAndScores.png" height="545" width="130"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get all <b>teams</b> and scores'.</I></P>

<P><img alt="" src="images/greenCircle.png">
2.6. The 'Get the <b>teams</b> and scores by ranges' link: 
<a href="http://localhost:8280/company/teams/range?rangeFrom=1&rangeTo=3">
http://localhost:8280/company/teams/range?rangeFrom=1&rangeTo=3</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L78">
kp.company.handlers.TeamHandler::handleTeamsRangeByScore</a>.<br>
</P>
<P><img alt="" src="images/GetTeamsAndScoresByRanges.png" height="345" width="135"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get the <b>teams</b> and scores by ranges'.</I></P>

<P><img alt="" src="images/greenCircle.png">
2.7. The 'Get the <b>team</b> rank by team id' link: 
<a href="http://localhost:8280/company/teams/rank?id=1">
http://localhost:8280/company/teams/rank?id=1</a>.
</P>
<P>
The handler method:
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/company/handlers/TeamHandler.java#L115">
kp.company.handlers.TeamHandler::handleTeamRankById</a>.<br>
</P>
<P><img alt="" src="images/GetTeamRankByTeamId.png" height="30" width="30"/><br>
<img alt="" src="images/blackArrowUp.png">
<I>The result from the 'Get the <b>team</b> rank by team id'.</I></P>

<a href="#top">Back to the top of the page</a>
<hr>
<H3 id="THREE">❸ Java Client</H3>

<P>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/03%20MVN%20clean%20install%20execute%20client.bat">
 <I>"03 MVN clean install execute client.bat"</I></a> launch the Java client.<br>
<img alt="" src="images/orangeHR-500.png"></P>

<P><img alt="" src="images/greenCircle.png">
3.1. The client application <a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/WebClientLauncher.java">
kp.client.WebClientLauncher</a>.<br>
The method 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/WebClientLauncher.java#L58">
kp.client.WebClientLauncher::performRequests</a> starts three subscribers:<br>
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/DepartmentSubscriber.java">DepartmentSubscriber</a>, 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/EmployeeSubscriber.java">EmployeeSubscriber</a>, 
<a href="https://github.com/k1729p/Study03/blob/main/src/main/java/kp/client/subscribers/TeamSubscriber.java">TeamSubscriber</a>.
</P>
<P>
The <a href="images/ScreenshotJavaClient.png">
<b>screenshot</b></a>
of the console log from the run of the batch file <b>"03 MVN clean install execute client.bat"</b>
</P>

<a href="#top">Back to the top of the page</a>
<hr>
<H3 id="FOUR">❹ Curl Client</H3>

<P>Action:<br>
<img alt="" src="images/orangeHR-500.png"><br>
<img alt="" src="images/orangeSquare.png"> 1. With batch file
 <a href="https://github.com/k1729p/Study03/blob/main/0_batch/04%20CURL%20call%20server.bat">
 <I>"04 CURL call server.bat"</I></a> load the sample dataset and get departments and employees.<br>
<img alt="" src="images/orangeHR-500.png"></P>

<P><img alt="" src="images/greenCircle.png">
4.1. The <a href="images/ScreenshotCurlCallServer.png">
<b>screenshot</b></a>
of the console log from the run of the batch file <b>"04 CURL call server.bat"</b>
</P>

<a href="#top">Back to the top of the page</a>
<hr>
</BODY>
</HTML>