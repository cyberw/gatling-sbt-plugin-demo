enablePlugins(GatlingPlugin)

scalaVersion := "2.12.7"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.3" % "test,it"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "3.0.4-SNAPSHOT" % "test,it"
libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.6.1"
