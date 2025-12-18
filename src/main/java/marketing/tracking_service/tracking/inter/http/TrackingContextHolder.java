package marketing.tracking_service.tracking.inter.http;


public final class TrackingContextHolder {
    private static final ThreadLocal<TrackingContext> CTX = new ThreadLocal<>();

    private TrackingContextHolder() {}

    public static void set(TrackingContext ctx) { CTX.set(ctx); }
    public static TrackingContext get() { return CTX.get(); }
    public static void clear() { CTX.remove(); }
}
