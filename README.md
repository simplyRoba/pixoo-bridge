# Pixoo Bridge
Small docker image to communicate with a network enabled pixoo LED dot matrix.

<!-- TODO Downloads from ghcr.io -->
![GitHub License](https://img.shields.io/github/license/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Fblob%2Fmain%2FLICENSE)
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/simplyRoba/pixoo-bridge/run-tests.yaml?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Factions%2Fworkflows%2Frun-tests.yaml%3Fquery%3Dbranch%253Amain)
![GitHub release (with filter)](https://img.shields.io/github/v/release/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Freleases)
![GitHub issues](https://img.shields.io/github/issues/simplyRoba/pixoo-bridge?link=https%3A%2F%2Fgithub.com%2FsimplyRoba%2Fpixoo-bridge%2Fissues)
![GitHub Repo stars](https://img.shields.io/github/stars/simplyRoba/pixoo-bridge)

# Configuration
Pass key as environment variable the docker container.

| Key                          | Default value | Description                                       |
|------------------------------|---------------|---------------------------------------------------|
| PIXOO_HOST                   | localhost     | IP of the pixoo in the network                    |
| PIXOO_SIZE                   | 64            | screen size of the pixoo                          |
| PIXOO_HEALTH_FORWARD         | true          | Ping pixoo during image health check              |
| PIXOO_DOCS_ENABLED           | true          | Enable API documentation                          |
| PIXOO_ANIMATION_SPEED_FACTOR | 1.4           | Factor to adjust animation speed of uploaded gifs |

# Run the image
```shell
docker run -p 4000:4000 -e "PIXOO_HOST=xxx.xxx.xxx.xxx" ghcr.io/simplyroba/pixoo-bridge:latest
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

# Further resources
## Pixoo API
[Official Divoom API documentation](http://doc.divoom-gz.com/web/#/12?page_id=191)
