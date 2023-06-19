# pixoo-bridge
Small docker image to communicate with a network enabled pixoo LED dot matrix.

# API
[Docs](http://doc.divoom-gz.com/web/#/12?page_id=191)

# Run the image
```shell
docker run -p 8080:8080 -e "PIXOO_HOST=xxx.xxx.xxx.xxx" ghcr.io/simplyroba/pixoo-bridge:latest
```