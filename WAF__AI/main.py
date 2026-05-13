from datasets import load_dataset
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from transformers import Trainer, TrainingArguments
import pandas as pd


# 1. LOAD CSV

dataset = load_dataset("csv", data_files="rsmt_traces_dataset.csv")


# 2. USE ONLY NEEDED COLUMNS

def preprocess(example):
    return {
        "text": example["payload"],
        "label": 0 if example["label"] == "norm" else 1
    }

dataset = dataset["train"].map(preprocess)

# split
dataset = dataset.train_test_split(test_size=0.2)


# 3. TOKENIZER

tokenizer = AutoTokenizer.from_pretrained("distilbert-base-uncased")

def tokenize(example):
    return tokenizer(example["text"], truncation=True, padding="max_length")

dataset = dataset.map(tokenize, batched=True)


# 4. MODEL

model = AutoModelForSequenceClassification.from_pretrained(
    "distilbert-base-uncased",
    num_labels=2
)


# 5. TRAINING (AdamW used internally)

training_args = TrainingArguments(
    output_dir="./results",
    num_train_epochs=2,
    per_device_train_batch_size=8,
    per_device_eval_batch_size=8,
    evaluation_strategy="epoch",
    logging_dir="./logs",
    learning_rate=5e-5,   # AdamW LR
    weight_decay=0.01     # AdamW feature
)

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=dataset["train"],
    eval_dataset=dataset["test"]
)


# 6. TRAIN

trainer.train()


# 7. SAVE MODEL

trainer.save_model("./trained_model")
tokenizer.save_pretrained("./trained_model")