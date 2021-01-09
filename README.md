Exercise:

Imagine you have any number of servers (1 to 1000+) that generate log files for your distributed app. Each log file can range from 100MB - 512GB in size. They are copied to your machine which contains only 16GB of RAM.

The local directory would look like this:
/temp/server-ac329xbv.log
/temp/server-buyew12x.log
/temp/server-cnw293z2.log

Our goal is to print the individual lines out to your screen, sorted by timestamp.
.....

A log file stuctured as a `CSV` with the date in ISO 8601 format in the first column and an event in the second column.

Each individual file is already in time order.

As an example, if file /temp/server-bc329xbv.log looks like:

    2016-12-20T19:00:45Z, Server A started.
    2016-12-20T19:01:25Z, Server A completed job.
    2016-12-20T19:02:48Z, Server A terminated.

And file /temp/server-cuyew12x.log looks like:

    2016-12-20T19:01:16Z, Server B started.
    2016-12-20T19:03:25Z, Server B completed job.
    2016-12-20T19:04:50Z, Server B terminated.

Then our output would be:

    2016-12-20T19:00:45Z, Server A started.
    2016-12-20T19:01:16Z, Server B started.
    2016-12-20T19:01:25Z, Server A completed job.
    2016-12-20T19:02:48Z, Server A terminated.
    2016-12-20T19:03:25Z, Server B completed job.
    2016-12-20T19:04:50Z, Server B terminated.