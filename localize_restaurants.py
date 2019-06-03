import random
import sys
from pymongo import MongoClient


def insert_localized_restauants():
    print(
        "Enter co-ordinates in the format {latitude},{longitude}. For ex: 12.01,27.66"
    )
    try:
        lat, lng = map(float, input().split(","))
    except:
        print(sys.exc_info())
        sys.exit("Input format incorrect.")

    client = MongoClient()
    db = client["restaurant-database"]
    restaurant_collection = db["restaurants"]
    num_restaurants = restaurant_collection.count_documents({})

    start_index = round(random.random() * (num_restaurants / 2))
    num_restaurants_to_change = 50

    cursor = db["restaurants"].find(skip=start_index, limit=num_restaurants_to_change)

    for restaurant in cursor:
        restaurant["latitude"] = lat
        restaurant["longitude"] = lng
        restaurant_collection.find_one_and_replace(
            {"_id": restaurant["_id"]}, restaurant
        )

    print("Restaurants around co-ordinates created.")


if __name__ == "__main__":
    insert_localized_restauants()
