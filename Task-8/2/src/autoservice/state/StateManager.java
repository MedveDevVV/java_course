package autoservice.state;

import java.io.*;

public class StateManager {
    private static final String STATE_FILE = "autoservice_state.dat";

    public static void saveState(ApplicationState state) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(state);
        }
    }

    public static ApplicationState loadState() throws IOException, ClassNotFoundException {
        File file = new File(STATE_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ApplicationState(); // Возвращаем пустое состояние
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            return (ApplicationState) ois.readObject();
        }
    }

    public static boolean stateFileExists() {
        return new File(STATE_FILE).exists();
    }

    public static void deleteStateFile() {
        File file = new File(STATE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}