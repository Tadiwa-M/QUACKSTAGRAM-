import java.util.ArrayList;
import java.util.List;

public class NotificationsManager implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void addNotification(String notification) {
        notifications.add(notification);
        notifyObservers();
    }

    public List<String> getNotifications() {
        return notifications;
    }
}
