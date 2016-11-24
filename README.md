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
    
### eventually

#### general
 - [ ] admin privs for households
 - [ ] copy chore from one household to another
 - [ ] hash/salt passwords
 - [ ] authorization
 - [ ] refactor code to use only `-` instead of `_`
 - [ ] refactor db intensive actions to use rollback transactions (ex: signup with default house setup)
 - [ ] actual redirecting not rendering login page on un-auth home route
 - [ ] script to run on mockaroo scripts in the right order 

#### migrations
 - [ ]  add no null values to user_name  
 - [ ]  rename pass to password people table
 - [ ]  refactor db/col names to use `-` instead of `_` (add escaping `"quotes"`)
 - [ ]  take off unique constraint on household names 
 - [ ]  change chart table to use living situation id instead of household and person id
 - [ ]  add on delete cascade clause
