# toggl-reporter

![screenshot](assets/screencast.gif)

Tiny SpringBoot-Application, build with vaadin and mongodb, that pulls your TimeEntries of your workspace from [toggl](https://toggl.com).
The stored information allow a fine grained reporting and analysis that aren't possible within toggl.
Some Report examples:
* Daily working hours of each team-member in comparison
* Worked hours within week per each member
* earned money, average hours per day etc.

Why we've build this app: As a small software mill we need to track your employee's time and perform some checks. Furthermore detailed reportings and performance indicators encourage everybody within the team.

## usage

Mainly we've designed the application to run within docker. We've provided an image [rocketbaseio/toggl-reporter](https://hub.docker.com/r/rocketbaseio/toggl-reporter/).

```shell
# shell command to get it run within docker
docker run -ti --rm -e SPRING_DATA_MONGODB_URI=mongodb://mongo/toggl-report -p 8080:8080 --link mongo:mongo rocketbaseio/toggl-reporter
# link the application to a mongo-container and configure datebase settings
```

At first run an **admin** user with password **admin** wil get created. After login the application ask's for an toggl-api-token. All configurations, tokens etc. will get stored to your mongodb.

***It's recommended to change the password of the admin-user!***

## configuration

* SPRING_DATA_MONGODB_URI *(for example mongodb://mongo/toggl-report)*
* APPLICATION_TITLE *(will get displayed on login screen and header or menu)*
* default spring boot parameters like port etc.

## remarks

this is an initial version - no warranties etc...

