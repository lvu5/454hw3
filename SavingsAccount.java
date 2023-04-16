public class SavingsAccount {
    int balance;
    int preferredWaiting;
    Lock lock = new ReentrantLock();
    Condition preferedCondtion = lock.newCondition();
    Condition ordinaryCondition = lock.newCondition();

    void withdraw(boolean preferred, int amount) throws InterruptedException {
        lock.lock();
        try {
            if (preferred) {
                preferredWaiting++;
                while (balance < amount) {
                    preferredCondition.await();
                }
                preferredWaiting--;
                balance -= amount;
                if (preferredWaiting == 0)
                    preferredCondition.signal();
                else
                    ordinaryCondition.signal();
            } else {
                while (balance < amount) {
                    ordinaryCondition.await();
                }
                balance -= amount;
                if (preferredWaiting == 0)
                    preferredCondition.signal();
                else
                    ordinaryCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }
    void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
            if (preferredWaiting == 0)
                preferredCondition.signal();
            else
                ordinaryCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
