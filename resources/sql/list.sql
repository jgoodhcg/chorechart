-- :name list-households :? :*
-- :doc given a person's id lists all associated households
select households.id as household_id, households.house_name,
living_situations.id as living_situation_id
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
-- :doc given a household_id  and a start date list all chores with the people who did them from date
select people.user_name  as user_name,
       chores.chore_name as chore_name,
       chores.id         as chore_id,
       chart.id          as chart_id,
       chart.moment      as moment
from chart
inner join chores
on chores.id = chart.chore_id
inner join living_situations
on living_situations.household_id = :household_id
and chart.living_situation_id = living_situations.id
inner join people
on people.id = living_situations.person_id
where chart.moment >= :start::date
and chart.moment <= :end::date
order by chart.moment

-- :name list-roomates :? :*
-- :doc given a living_situation_id return a list of people living in the same household
with household_id_to_match
as (select living_situations.household_id as id
   from living_situations
   where living_situations.id = :living_situation_id)
select people.user_name as user_name,
people.id as person_id,
living_situations.id as living_situation_id
from living_situations
inner join people
on people.id = living_situations.person_id
where living_situations.household_id
in (select id from household_id_to_match)
