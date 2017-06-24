# Chore Chart
Chore Chart is a simple clojure(script) application using reframe. Meant as a test pilot for learning reframe development and something moderately useful.

## email (dev|production)
1. To support email provide the chorechart gmail password in `config.edn` at the base of this project
    - include this in file `{:mail-password "password.here"}`

## dev start
1. install Vagrant + Virtualbox 
2. clone repo
3. run `vagrant up` in cloned directory
4. wait

### regular repl  
5. open 2 shells and run `vagrant ssh` in both, after the box is initialized  
6. in 1 shell run `lein run` and wait until it is serving  
7. in the other shell run `lein figwheel`  
8. visit `localhost:3000` in browser  

### nrepl  
5. run `bash start.sh` to conenct to vm with ssh ?tunneled ports?  
6. run `cd/chorechart` to get into the project  
7. run `./cider-deps-repl` to create an nrepl that cider can connect to  
8. in spacemacs open a repl by going to a **.clj** file and running `SPC m s c` and selecting **localhost and  then chorechart:port 7000**  
9. open repl buffer `SPC b b` using arrow keys to select repl buffer  
10. run `(start)` to launch the webserver  
11. when that returns run `(start-fw)` to start figwheel  
12. when that returns run `(cljs)` to start a cljs nrepl  
13. visit `localhost:3000` in browser  

## mock data

1. `vagrant ssh` to gain access to the vm  
2. navigate to `~/chorechart/mockaroo`  
3. run the desired `.sql` scripts with the command `sudo -u postgres psql -U postgres -d chorechart_dev -a -f scriptname.sql`  

## connect to db
1. `vagrant ssh`
2. `sudo -u postgres psql -U postgres -d chorechart_dev ` 

## todo

### alpha_2
- [x] refactor namespaces
  - [x] SPA components
    - [x] chart
    - [x] households
    - [x] roomates
    - [x] chores
    - [x] info
    - [x] navbar
    - [x] other misc
  - [x] handlers
    - [x] chart
    - [x] households
    - [x] chores
    - [x] roomates
  - [x] endpoints
    - [x] make authentication middleware
    - [x] refactor home routes into logically grouped resty ns
      - [x] chores
      - [x] households
      - [x] living_situations
      - [x] roomates
      - [x] chart
- [x] chart filter
  - [x] filter buttons
  - [x] interval selector
    - [x] interval inputs
    - [x] start interval dispatch
    - [x] end interval dispatch
  - [x] default interval 
  - [x] alter to accept intervval
    - [x] get-chart handler
    - [x] endpoint
    - [x] sql query 
- [x] change roomates to people in SPA
- [x] basic error messages in SPA
  - [x] add roomate
  - [x] households
  - [x] chores
- [x] validate email on signup http://stackoverflow.com/a/33737528/5040125 
- [x] splash screen
  
### alpha_3
- [x] remove name from chart entry input
- [x] fix chart submission sticking after entry
- [x] fix chore list not refreshing when switching household and going directly to chart
- [x] safari squashes date field for chart entry
- [x] house selection should be a click event on the whole row, not tiny box that safari doesn't even render
- [x] hash passwords
- [x] error message on invite
- [x] case insesitive emails
- [x] update readme

### alpha_4
- [x] margin on bottom of chart to go above absolutely placed input section
- [x] unique constraint on living situations to avoid duplicates
- [ ] send an email after signing up
    - [x] [try this](https://nakkaya.com/2009/11/10/using-java-mail-api-from-clojure/)
        - [x] did not work
    - [x] [this worked](https://linuxconfig.org/configuring-gmail-as-sendmail-email-relay)
- [ ] password recovery
    - [ ] forgot password enpoint
        - [ ] set random identifier on users table with timestamp
        - [ ] send recovery email with link
    - [ ] password reset endpoint GET
        - [ ] check identifier from GET param
        - [ ] check timestamp
        - [ ] show password recovery form (embed identifier in POST) or Error
    - [ ] password reset endpiont POST
        - [ ] check identifier from POST
        - [ ] check timestamp
        - [ ] reset password or error
- [ ] invite friend
    - [ ] send email
    - [ ] add to house if already a member
    - [ ] add to house on signup (or prompt?)

### beta_1
- [ ] material design
- [ ] test suite

### beta_2
- [ ] follow all these rules
- [ ] authorization
- [ ] optional image upload for verification on chart entries
- [ ] fake deleting (inactive bools)
  - [ ] chores
  - [ ] living situations (use this to allow "soft kicks" that a user can recover from)
- [ ] kebab the snakes `a_snake_example a-kebab-example`
  - [ ] remove all snakes from SPA components and handlers
  - [ ] remove all snakes from endpoint params
  - [ ] only snakes are in the keys to maps sent to db fn's OR kebab to snake library

### eventually
#### general
- [ ] add new household on click causes js error (can't find call)
- [ ] custom date ranges are zero'd but chart entry moments are not (makes them not entirely inclusive ranges)
- [ ] admin privs for households
- [ ] copy chore from one household to another
- [ ] redirect (signup/login) if logged in
- [ ] refactor db intensive actions to use rollback transactions (ex: signup with default house setup)
- [ ] adding roomate that isn't signed up
  - [ ] save as pending in postgres
  - [ ] send email to both inviter and invitee
  - [ ] spa notification to inviter
- [ ] add better re-frame dev tools
- [ ] get rid of the need for list returns on endpoints
- [ ] point system
  - [ ] value each chore
  - [ ] cumulative points listed next to roomates
- [ ] color code roomates
  - [ ] choose color on roomates page
  - [ ] chart displays colors
- [ ] password recovery
#### migrations
- [ ] rename pass to password people table
- [ ] add a first/last name to the people table
- [ ] constraint for no duplicate names on chores per household
- [ ] uniqueue constraint on living_situations `(person_id, household_id)`
- [ ] ?constraints for adding chores to households you don't belong to
- [ ] timestamp all database writes (add columns to all tables)
