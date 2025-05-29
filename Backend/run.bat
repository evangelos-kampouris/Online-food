@echo off
set CP=.;gson.jar;src

start "Master"   cmd /k java -cp "%CP%" Entity.Master  0.0.0.0 9000
start "Reducer"  cmd /k java -cp "%CP%" Entity.Reducer 0.0.0.0 8080 0.0.0.0 9000
start "Worker1"  cmd /k java -cp "%CP%" Entity.Worker  0.0.0.0 9001 0.0.0.0 8080
start "Worker2"  cmd /k java -cp "%CP%" Entity.Worker  0.0.0.0 9002 0.0.0.0 8080
start "Worker3"  cmd /k java -cp "%CP%" Entity.Worker  0.0.0.0 9003 0.0.0.0 8080
start "Manager"  cmd /k java -cp "%CP%" User.Manager     0.0.0.0 9000