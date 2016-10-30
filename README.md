##start

1. install Vagrant + Virtualbox 
2. clone repo
3. run `vagrant up` in cloned directory
4. wait
5. open 2 shells and run `vagrant ssh` in both, after the box is initialized
6. in 1 shell run `lein run` and wait until it is serving
7. in the other shell run `lein figwheel` 
8. visit `localhost:3000` in browser

##todo

- [ ] simple chore submission
  - [ ] create form that submits
    - [x] form components
    - [x] backend route
    - [x] form ajax submission
    - [x] database migrations
      - [x] chore chart table
    - [x] database action
    - [x] backend route writes to db
    - [ ] test
      - [ ] sql 
      - [ ] form submission
      - [ ] back end route
      
- [ ] add account functionality
  - [ ] migrations
    - [ ] account table
    - [ ] people table for the N -> 1 relationship people -> account
    - [ ] person id in chart table
  - [ ] account creation
  - [ ] person creation
  - [ ] person login
    
- [ ] figure out how to get nrepl for clojure/cljs
