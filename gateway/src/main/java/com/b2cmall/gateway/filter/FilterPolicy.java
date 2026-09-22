package com.b2cmall.gateway.filter;
/** Shared request attributes and ordering, before route StripPrefix. */
final class FilterPolicy {
    static final String WHITE_URL=FilterPolicy.class.getName()+".whiteUrl";
    static final int WHITE_ORDER=-120, AUTH_ORDER=-110, LOG_ORDER=-100;
    private FilterPolicy(){}
}
