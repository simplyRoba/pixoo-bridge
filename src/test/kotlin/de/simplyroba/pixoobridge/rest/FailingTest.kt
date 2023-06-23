package de.simplyroba.pixoobridge.rest

import org.junit.jupiter.api.Test

class FailingTest {

  @Test
  fun `boom`() {
    throw RuntimeException("boom")
  }
}