import pandas as pd
import joblib


MODEL_PATH = "bio_os_action_model.pkl"
LABEL_ENCODER_PATH = "bio_os_label_encoder.pkl"


def predict_action(water, light, temperature):
    model = joblib.load(MODEL_PATH)
    label_encoder = joblib.load(LABEL_ENCODER_PATH)

    input_data = pd.DataFrame(
        [[water, light, temperature]],
        columns=["waterInput", "light", "temperature"]
    )

    prediction_encoded = model.predict(input_data)[0]
    prediction_label = label_encoder.inverse_transform([prediction_encoded])[0]

    return prediction_label


def main():
    print("=== Bio-OS AI Prediction Test ===")

    test_cases = [
        (5.0, 85.0, 34.0),
        (15.0, 80.0, 32.0),
        (120.0, 75.0, 26.0),
        (150.0, 80.0, 28.0),
        (60.0, 40.0, 20.0),
    ]

    for water, light, temperature in test_cases:
        action = predict_action(water, light, temperature)

        print(
            f"Water={water}, Light={light}, Temp={temperature} "
            f"=> Predicted Action: {action}"
        )

    print("\n=== Prediction Complete ===")


if __name__ == "__main__":
    main()