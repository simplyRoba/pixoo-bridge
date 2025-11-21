# Pixoo Bridge
Small docker image to communicate with a network enabled pixoo LED dot matrix.

<!-- TODO Downloads from ghcr.io -->
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
![GitHub License](https://img.shields.io/github/license/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Fblob%2Fmain%2FLICENSE)
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/simplyRoba/pixoo-bridge/run-tests.yaml?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Factions%2Fworkflows%2Frun-tests.yaml%3Fquery%3Dbranch%253Amain)
[![GitHub release (with filter)](https://img.shields.io/github/v/release/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Freleases)](https://github.com/simplyRoba/pixoo-bridge/releases)
[![GitHub issues](https://img.shields.io/github/issues/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Fissues)](https://github.com/simplyRoba/pixoo-bridge/issues)
![GitHub Repo stars](https://img.shields.io/github/stars/simplyRoba/pixoo-bridge)


# Configuration
Pass key as environment variable the docker container.

| Key                          | Default value    | Description                                       |
|------------------------------|------------------|---------------------------------------------------|
| PIXOO_BASE_URL               | http://localhost | Protocol and IP of the pixoo in the network       |
| PIXOO_SIZE                   | 64               | screen size of the pixoo                          |
| PIXOO_ANIMATION_SPEED_FACTOR | 1.4              | Factor to adjust animation speed of uploaded gifs |
| PIXOO_BRIDGE_PORT            | 4000             | Port of the pixxo bridge in the container         |
| PIXOO_BRIDGE_HEALTH_FORWARD  | true             | Ping pixoo during image health check              |
| PIXOO_BRIDGE_DOCS_ENABLED    | true             | Enable API documentation (Swagger UI)             |
| PIXOO_BRIDGE_MAX_IMAGE_SIZE  | 2MB              | Image size for uploads. Format like 128KB or 5MB  |
| PIXOO_BRIDGE_LOG_LEVEL       | INFO             | Possible values OFF, DEBUG, INFO, WARN, ERROR     |

# Run the image
replace the ip of your pixoo and run
```shell
docker run -p 4000:4000 -e "PIXOO_BASE_URL=xxx.xxx.xxx.xxx" ghcr.io/simplyroba/pixoo-bridge:latest
```
or use the [docker-compose.yaml](/docker-compose.yaml)
```shell
docker compose up -d
```

# API
## Documentation
Generated API documentation can be reached under 
```
http://localhost:4000/swagger-ui/index.html
```
![Swagger Screenshot](/docs/swagger-screenshot.png)

## Limitations
The `Channel control API` of the Pixoo will not be implemented. Use the App for these functionality.

# Migration Guides
## from 0.x to 1.x
Rename configuration parameter in your `compose.yaml`.

| old name   | new name       |
|------------|----------------|
| PIXOO_HOST | PIXOO_BASE_URL | 

## from 1.x to 2.x
Rename configuration parameter in your `compose.yaml:` 

| old name             | new name                    |
|----------------------|-----------------------------|
| PIXOO_HEALTH_FORWARD | PIXOO_BRIDGE_HEALTH_FORWARD |
| PIXOO_DOCS_ENABLED   | PIXOO_BRIDGE_DOCS_ENABLED   |
| PIXOO_MAX_IMAGE_SIZE | PIXOO_BRIDGE_MAX_IMAGE_SIZE |
| PIXOO_LOG_LEVEL      | PIXOO_BRIDGE_LOG_LEVEL      |


# Further resources
## Official product page
[Pixoo64](https://divoom.com/products/pixoo-64)

## Pixoo API
[Official Divoom API documentation](http://doc.divoom-gz.com/web/#/12?page_id=191)
