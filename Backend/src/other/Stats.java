package other;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Stats implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> stats = new HashMap<>(); //store_name, value

    public Stats() {}

    public Stats(String store_name, int sales) {
        stats.put(store_name, sales);
    }

    public synchronized void addStat(String store_name, int sales) {
        if(stats.containsKey(store_name)) {
            stats.put(store_name, stats.get(store_name) + sales);
            return;
        }
        stats.put(store_name, sales);
    }

    public int totalSales() {
        return stats.values().stream().reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int sum = 0;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            sb.append(String.format("- %s: %d\n", entry.getKey(), entry.getValue()));
            sum += entry.getValue();
        }

        sb.append(String.format("Total Sales: %d\n", sum));
        return sb.toString();
    }
}
