import sys
import time
import random
from random import seed

def getClientScore(age, gender):
    seed(time.time())
    maxClientScore = 60 + age + (5 if gender == "M" else 10)
    return round(random.uniform(35, maxClientScore), 2)

age = int(sys.argv[1])
gender = sys.argv[2]

print(getClientScore(age, gender))