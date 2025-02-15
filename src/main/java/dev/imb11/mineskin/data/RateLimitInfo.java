package dev.imb11.mineskin.data;

public record RateLimitInfo(NextRequest next, DelayInfo delay, LimitInfo limit) {
}
