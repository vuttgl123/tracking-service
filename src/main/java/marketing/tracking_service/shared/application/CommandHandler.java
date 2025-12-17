package marketing.tracking_service.shared.application;

public interface CommandHandler<C extends Command, R extends Result> {
    R handle(C command);
}
