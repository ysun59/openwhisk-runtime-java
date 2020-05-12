#!/usr/bin/python3

import pymongo

myclient = pymongo.MongoClient("mongodb://r630-01:27017/")

mydb = myclient["mydatabase"]
mycol = mydb["customers"]

print(mycol.insert_one({ "username": "username1", "password": "password1" }).inserted_id)
print(mycol.insert_one({ "username": "username2", "password": "password2" }).inserted_id)
print(mycol.insert_one({ "username": "username3", "password": "password3" }).inserted_id)
print(mycol.insert_one({ "username": "username4", "password": "password4" }).inserted_id)
print(mycol.insert_one({ "username": "username5", "password": "password5" }).inserted_id)

for x in mycol.find({ "username": "username1" }):
    print(x)
