import org.json.JSONObject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SecretFinder {

    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        // Read input.json from the current directory
        String content = new String(Files.readAllBytes(Paths.get("input.json")));
        JSONObject json = new JSONObject(content);

        JSONObject keys = json.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<Point> points = new ArrayList<>();

        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject obj = json.getJSONObject(key);

            int base = obj.getInt("base");
            String valStr = obj.getString("value");
            BigInteger y = new BigInteger(valStr, base);

            points.add(new Point(BigInteger.valueOf(x), y));
        }

        Map<BigInteger, Integer> frequencyMap = new HashMap<>();
        List<List<Point>> combinations = generateCombinations(points, k);

        for (List<Point> comb : combinations) {
            BigInteger secret = lagrangeInterpolationConstant(comb);
            frequencyMap.put(secret, frequencyMap.getOrDefault(secret, 0) + 1);
        }

        // Most frequent constant is the answer
        BigInteger result = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        System.out.println("Secret: " + result);
    }

    // Lagrange interpolation to find f(0)
    static BigInteger lagrangeInterpolationConstant(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                BigInteger xj = points.get(j).x;

                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator);
            term = term.divide(denominator);
            result = result.add(term);

        }

        return result;
    }


    // Generate all combinations of size k
    static List<List<Point>> generateCombinations(List<Point> arr, int k) {
        List<List<Point>> result = new ArrayList<>();
        backtrack(arr, result, new ArrayList<>(), 0, k);
        return result;
    }

    static void backtrack(List<Point> arr, List<List<Point>> result, List<Point> temp, int start, int k) {
        if (temp.size() == k) {
            result.add(new ArrayList<>(temp));
            return;
        }

        for (int i = start; i < arr.size(); i++) {
            temp.add(arr.get(i));
            backtrack(arr, result, temp, i + 1, k);
            temp.remove(temp.size() - 1);
        }
    }
}
