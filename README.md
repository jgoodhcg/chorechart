# Chore Chart

## start

1. install Vagrant + Virtualbox 
2. clone repo
3. run `vagrant up` in cloned directory
4. wait

### non connectable repl way  
5. open 2 shells and run `vagrant ssh` in both, after the box is initialized  
6. in 1 shell run `lein run` and wait until it is serving  
7. in the other shell run `lein figwheel`  

### connectable repl way  
5. run `bash start.sh` to conenct to vm with ssh ?tunneled ports?  
6. run `cd/chorechart` to get into the project  
7. run `./cider-deps-repl` to create an nrepl that cider can connect to  
8. in spacemacs open a repl by going to a **.clj** file and running `SPC m s c` and selecting **localhost and  then chorechart:port 7000**  
9. open repl buffer `SPC b b` using arrow keys to select repl buffer  
10. run `(start)` to launch the webserver  
11. when that returns run `(start-fw)` to start figwheel  
12. when that returns run `(cljs)` to start a cljs nrepl  

8/13. visit `localhost:3000` in browser  

## mock data

1. `vagrant ssh` to gain access to the vm  
2. navigate to `~/chorechart/mockaroo`  
3. run each of the `.sql` scripts in the current order of [people, households, living_situations, chores, chart] 
with the command `sudo -u postgres psql -U postgres -d chorechart_dev -a -f scriptname.sql`  
expect some commands to fail due to the nature of random data generation and relationships between tables  

## todo

### mvp
 - [x] remove all data from db
 - [x] migration improvement
   - [x] remove unique constraint on household_name
   - [x] remove unique constraint on user_name
   - [x] add email unique constraint
 - [x] populate database with single household with multiple people
 - [x] signup page
   - [x] email first
   - [x] handle error
 - [x] login page
   - [x] email as login credential
 - [x] households page
   - [x] add household
   - [x] display households
   - [x] select household
   - [x] better default select household
   - [x] delete household
   - [x] rename household
 - [x] roomates page
   - [x] view roomates for selected household
   - [x] invite user to household
 - [ ] chores page (works on selected household)
   - [ ] list chores
   - [ ] remove chore
   - [ ] add chore
   - [ ] edit chore
 - [ ] chart page
 - [ ] logout in navbar

### eventually

#### general
 - [ ] admin privs for households
 - [ ] copy chore from one household to another
 - [ ] hash/salt passwords
 - [ ] authorization
 - [ ] refactor code to use only `-` instead of `_`
 - [ ] redirect (signup/login) if logged in
 - [ ] refactor db intensive actions to use rollback transactions (ex: signup with default house setup)
 - [ ] actual redirecting not rendering login page on un-auth home route
 - [ ] script to run on mockaroo scripts in the right order 
 - [ ] display first name in the chart
 - [ ] pending household_edit should have some color indication when it is successful
 - [ ] get rid of tables for households listing
 - [ ] adding roomate that isn't signed up
   - [ ] save as pending in postgres
   - [ ] send email to both inviter and invitee
   - [ ] spa notification to inviter
 - [ ] chore page
   - [ ] list all chores from all households
   - [ ] copy chore
 - [ ] renaming household page does not update selected house for roomate page
 

#### migrations
 - [ ] rename pass to password people table
 - [ ] refactor db/col names to use `-` instead of `_` (add escaping `"quotes"`) OR use the kebab library mentioned in luminus docs
 - [ ] add on delete cascade clause
 - [ ] add a first/last name to the people table
 - [ ] constraint for no duplicate names on chores per household
 - [ ] unieuqe constraint on living_situations `(person_id, household_id)`
 - [ ] constraints for adding chores to households you don't belong to
