package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.bridge.draw.model.*
import de.simplyroba.pixoobridge.client.PixooClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Draw")
@RestController
@RequestMapping("/draw")
class DrawController(private val imageService: ImageService, private val pixooClient: PixooClient) {

  @Operation(description = "Fill complete screen with rgb color")
  @PostMapping("/fill", consumes = [APPLICATION_JSON_VALUE])
  fun fill(@RequestBody body: FillRequest): ResponseEntity<Unit> {
    if (!body.validate()) return ResponseEntity.badRequest().build()

    imageService.drawColor(body)
    return ok().build()
  }

  @Operation(description = "Upload an image and show it")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Image to upload. Supported formats: jpg, jpeg, png, gif"
  )
  @PostMapping("/upload", consumes = [MULTIPART_FORM_DATA_VALUE])
  fun uploadImage(
    @RequestPart("image", required = true) image: MultipartFile
  ): ResponseEntity<Unit> {
    imageService.drawImage(image.resource)
    return ok().build()
  }

  @Operation(description = "")
  @PostMapping("/text")
  fun drawText(@RequestBody body: TextRequest): ResponseEntity<Unit> {
    if (!body.validate()) return ResponseEntity.badRequest().build()

    pixooClient.writeText(
      id = body.id,
      x = body.position.x,
      y = body.position.y,
      direction = body.scrollDirection.key,
      font = body.font,
      textWidth = body.textWidth,
      text = body.text,
      speed = body.scrollSpeed,
      color = body.color.toHexString(),
      align = body.textAlignment.key
    )
    return ok().build()
  }

  @Operation(description = "Clear all text")
  @PostMapping("/text/clear")
  fun clearText(): ResponseEntity<Unit> {
    pixooClient.clearText()
    return ok().build()
  }
}
