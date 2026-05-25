import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score, classification_report
import joblib


CSV_PATH = "bio_os_tick_history.csv"
MODEL_PATH = "bio_os_action_model.pkl"
LABEL_ENCODER_PATH = "bio_os_label_encoder.pkl"


def main():
    print("=== Bio-OS AI Training Start ===")

    df = pd.read_csv(CSV_PATH)

    print("\n[Dataset Preview]")
    print(df.head())

    print("\n[Dataset Info]")
    print(df.info())

    print("\n[Action Distribution]")
    print(df["lastAction"].value_counts())

    # 입력 데이터
    X = df[["waterInput", "light", "temperature"]]

    # 예측할 값
    y = df["lastAction"]

    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)

    if len(df) < 5:
        print("\n[Warning] 데이터가 너무 적어. 최소 20~100줄 이상 추천!")
        return

    if len(label_encoder.classes_) < 2:
        print("\n[Warning] lastAction 종류가 1개뿐이야.")
        print("AI가 비교해서 배울 대상이 부족함.")
        print("Pruning / Stable / Recovery 같은 결과가 섞이게 데이터를 더 만들어야 해.")
        return

    class_counts = df["lastAction"].value_counts()
    use_stratify = class_counts.min() >= 2

    if use_stratify:
        stratify_option = y_encoded
    else:
        stratify_option = None
        print("\n[Warning] 어떤 클래스는 데이터가 1개뿐이라 stratify 없이 분할합니다.")

    class_counts = df["lastAction"].value_counts()

    if class_counts.min() >= 2:
        stratify_option = y_encoded
        print("\n[Split Mode] Startified split enabled.")
    else:
        stratify_option = None
        print("\n[Split Mode] Stratified split disabled because at least one class has only 1 sample.")

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y_encoded,
        test_size=0.2,
        random_state=42,
        stratify=stratify_option
    )

    model = DecisionTreeClassifier(
        max_depth=4,
        random_state=42,
        class_weight="balanced"
    )

    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    accuracy = accuracy_score(y_test, y_pred)

    print("\n[Training Result]")
    print("Accuracy:", accuracy)

    print("\n[Classification Report]")
    print(
        classification_report(
            y_test,
            y_pred,
            labels=list(range(len(label_encoder.classes_))),
            target_names=label_encoder.classes_,
            zero_division=0
        )
    )

    print("\n[Sample Predictions]")
    sample_inputs = pd.DataFrame(
        [
            [5.0, 85.0, 34.0],
            [15.0, 80.0, 32.0],
            [120.0, 75.0, 26.0],
            [150.0, 80.0, 28.0],
            [60.0, 40.0, 20.0],
        ],
        columns=["waterInput", "light", "temperature"]
    )

    sample_predictions = model.predict(sample_inputs)
    sample_labels = label_encoder.inverse_transform(sample_predictions)

    for row, label in zip(sample_inputs.values, sample_labels):
        water, light, temperature = row
        print(
            f"Water={water:.1f}, Light={light:.1f}, Temp={temperature:.1f} "
            f"=> Predicted Action: {label}"
        )

    joblib.dump(model, MODEL_PATH)
    joblib.dump(label_encoder, LABEL_ENCODER_PATH)

    print("\n[Saved Files]")
    print(MODEL_PATH)
    print(LABEL_ENCODER_PATH)

    print("\n=== Bio-OS AI Training Complete ===")


if __name__ == "__main__":
    main()