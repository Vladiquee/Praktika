<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Обчислення площі трикутника</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            max-width: 600px;
        }
        form {
            margin-bottom: 20px;
        }
        input[type="number"], button {
            padding: 10px;
            margin: 5px 0;
            font-size: 16px;
            width: 100%;
        }
        .result {
            font-size: 18px;
            color: green;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <h1>Обчислення площі трикутника</h1>
    <form method="POST">
        <label for="sideA">Довжина першої сторони (A):</label>
        <input type="number" id="sideA" name="sideA" step="0.01" required>
        
        <label for="sideB">Довжина другої сторони (B):</label>
        <input type="number" id="sideB" name="sideB" step="0.01" required>
        
        <label for="angle">Кут між сторонами (у градусах):</label>
        <input type="number" id="angle" name="angle" step="0.01" required>
        
        <button type="submit">Обчислити площу</button>
    </form>

    <?php
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        // Отримання даних із форми
        $sideA = $_POST['sideA'];
        $sideB = $_POST['sideB'];
        $angle = $_POST['angle'];

        // Перевірка введених даних
        if (is_numeric($sideA) && is_numeric($sideB) && is_numeric($angle) && $angle > 0 && $angle < 180) {
            // Перетворення кута з градусів у радіани
            $angleInRadians = deg2rad($angle);

            // Обчислення площі
            $area = 0.5 * $sideA * $sideB * sin($angleInRadians);

            echo "<div class='result'>Площа трикутника: " . round($area, 2) . " квадратних одиниць.</div>";
        } else {
            echo "<div class='result' style='color: red;'>Будь ласка, введіть коректні дані!</div>";
        }
    }
    ?>
</body>
</html>
