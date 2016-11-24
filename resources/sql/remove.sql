-- :name remove-chart-entry! :! :n
-- :doc given a chart_id for an entry, remove it
delete from chart
where id = :chart_id
