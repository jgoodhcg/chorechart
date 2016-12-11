-- :name remove-chart-entry! :! :n
-- :doc given a chart_id for an entry, remove it
delete from chart
where id = :chart_id

-- :name remove-living-situation! :! :n
-- :doc given a living_situation_id remove that entry from living_situations
delete from living_situations
where id = :living_situation_id

-- :name remove-chore! :! :n
-- :doc given a chore_id delete it
delete from chores
where id = :chore_id
