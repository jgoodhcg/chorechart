-- :name list-households :? :*
-- :doc given a person's id lists all associated households
select households.id, households.house_name
from households
inner join living_situations
on households.id = living_situations.household_id
where living_situations.person_id = :person_id

-- :name list-chores :? :*
-- :doc given a household_id lists all the chores that can be done
select *
from chores
where chores.household_id = :household_id

-- :name list-chart-entries :? :*
-- :doc given a household_id list all chores with the people who did them
select people.user_name as user_name,
chores.chore_name as chore_name,
chart.id as chart_id,
chart.person_id as person_id, chart.chore_id as chore_id,
chart.moment as moment
from chart
inner join people
on people.id = chart.person_id
inner join chores
on chores.id = chart.chore_id
where chart.household_id = :household_id
and chart.moment >= :date_from::date
order by chart.moment asc
