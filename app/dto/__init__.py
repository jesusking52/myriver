

def sorted_days_of_week_dto(days_of_week):
    if days_of_week is None:
        return []
    return sorted(days_of_week, key=lambda day_of_week: day_of_week.value)
