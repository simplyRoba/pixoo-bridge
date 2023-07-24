package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.bridge.draw.model.RGB
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
class DrawController(private val imageService: ImageService, private val textService: TextService) {

  @Operation(description = "Fill complete screen with rgb color")
  @PostMapping("/fill", consumes = [APPLICATION_JSON_VALUE])
  fun fill(@RequestBody body: RGB): ResponseEntity<Void> {
    if (body.red !in 0..255 || body.green !in 0..255 || body.blue !in 0..255)
      return ResponseEntity.badRequest().build()
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
  ): ResponseEntity<Void> {
    imageService.drawImage(image.resource)
    return ok().build()
  }

  @Operation(description = "")
  @PostMapping("/text")
  fun drawText(): ResponseEntity<Void> {

    return ok().build()
  }

  @Operation(description = "Clear all text")
  @PostMapping("/text/clear")
  fun clearText(): ResponseEntity<Void> {
    textService.clear()
    return ok().build()
  }
}
