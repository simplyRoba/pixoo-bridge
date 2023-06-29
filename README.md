# pixoo-bridge
Small docker image to communicate with a network enabled pixoo LED dot matrix.

# Configuration
Pass key as environment variable the docker container.

| Key                  | Default value | Description                          |
|----------------------|---------------|--------------------------------------|
| PIXOO_HOST           | localhost     | IP of the pixoo in the network       |
| PIXOO_HEALTH_FORWARD | true          | Ping pixxo during image health check |
| PIXOO_DOCS_ENABLED   | true          | Enable Swagger                       |

# Run the image
```shell
docker run -p 4000:4000 -e "PIXOO_HOST=xxx.xxx.xxx.xxx" ghcr.io/simplyroba/pixoo-bridge:latest
```

# Further resources
## API
[Official Divoom API documentation](http://doc.divoom-gz.com/web/#/12?page_id=191)
