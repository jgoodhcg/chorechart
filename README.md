##start

1. install Vagrant + Virtualbox 
2. clone repo
3. run `vagrant up` in cloned directory
4. wait

-- non connectable repl way
5. open 2 shells and run `vagrant ssh` in both, after the box is initialized
6. in 1 shell run `lein run` and wait until it is serving
7. in the other shell run `lein figwheel` 

-- connectable repl way
5. run `bash start.sh` to conenct to vm with ssh ?tunneled ports?
6. run `cd/chorechart` to get into the project
7. run `./cider-deps-repl` to create an nrepl that cider can connect to
8. in spacemacs open a repl by going to a **.clj** file and running `SPC m s c` and selecting **localhost and  then chorechart:port 7000**
9. open repl buffer `SPC b b` using arrow keys to select repl buffer
10. run `(start)` to launch the webserver
11. when that returns run `(start-fw)` to start figwheel
12. when that returns run `(cljs)` to start a cljs nrepl

8/13. visit `localhost:3000` in browser

##todo

###simple proof of concept
- [x] simple chore submission
  - [x] create form that submits
    - [x] form components
    - [x] backend route
    - [x] form ajax submission
    - [x] database migrations
      - [x] chore chart table
    - [x] database action
    - [x] backend route writes to db
      
- [x] figure out how to get nrepl for clojure/cljs

### mvp
#### functionality
- [x] migrations
- [ ] add account functionality
  - [x] add buddy-auth
    - [x] mock users in memory to test login adds session
    - [x] add middleware to SPA and test redirect
  - [x] person signup
  - [x] person login
  - [ ] household 
    - [ ] creation by person (automatic living situation entry)
    - [ ] person in living situation can **add/remove any another person**
- [ ] chore creation
- [ ] chore chart entry
#### styling
- [ ] password inputs
    
###eventually

 - [ ] admin privs for households
 - [ ] copy chore from one household to another
 - [ ] hash/salt passwords
 - [ ] add no null values to user_name
 - [ ] rename pass to password people table
 - [ ] authorization

