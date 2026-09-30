package com.slothub.common.web;

import java.time.Instant;

public record ErrorResponse(String message, Instant timestamp) {
}
