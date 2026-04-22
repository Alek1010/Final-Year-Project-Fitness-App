package com.example.w1965221_finalyearproject.client

object PreMadePrograms {

    fun getPrograms(): List<WorkoutProgram> {
        return listOf(
            WorkoutProgram(
                id = "upper_lower_4day",
                name = "4 Day Upper Lower",
                days = listOf(
                    WorkoutDay(
                        name = "Day 1 - Upper",
                        exercises = listOf(
                            "Push-ups  Лицеви опори",
                            "Incline Push-ups  Наклонени лицеви опори (краката повдигнати)",
                            "Dumbbell Flys  Флайс с дъмбели",
                            "Pull Ups  Набиране",
                            "Single Arm Dumbbell Row  Гребане с дъмбел с една ръка",
                            "Dumbbell Bicep Curl  Сгъване за бицепс с дъмбели",
                            "Dumbbell Tricep Kickback  Разгъване за трицепс с дъмбел назад",
                            "Dumbbell Lateral Raise  Странично повдигане с дъмбели"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 2 - Lower",
                        exercises = listOf(
                            "Ab Crunch  Коремни преси на пода",
                            "Calf Raise  Повдигане на пръсти на стъпало",
                            "Bulgarian Split Squat  Български клек",
                            "Reverse Lunge  Обратни напади",
                            "Lying Leg Curl with Band  Лег налегнало сгъване с ластик",
                            "Band Leg Extension  Изпъване на крака с ластик"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 3 - Upper",
                        exercises = listOf(
                            "Dumbbell Lat Pullover  Пуловър с дъмбел",
                            "Single Arm Dumbbell Row  Гребане с дъмбел с една ръка",
                            "Pull Ups  Набиране",
                            "Incline Push-ups  Наклонени лицеви опори (краката повдигнати)",
                            "Dumbbell Flyes  Флайс с дъмбели",
                            "Dumbbell Overhead Tricep Extension  Разгъване за трицепс над глава с дъмбел",
                            "Dumbbell Bicep Curl  Сгъване за бицепс с дъмбели"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 4 - Lower",
                        exercises = listOf(
                            "Band Leg Extension  Изпъване на крака с ластик",
                            "Lying Leg Curl with Band  Лег налегнало сгъване с ластик",
                            "Reverse Lunge  Обратни напади",
                            "Dumbbell RDL  Румънска тяга с дъмбел",
                            "Calf Raise  Повдигане на пръсти на стъпало"
                        )
                    )
                )
            ),

            WorkoutProgram(
                id = "three_day_program",
                name = "3 Day Program",
                days = listOf(
                    WorkoutDay(
                        name = "Day 1 - Upper",
                        exercises = listOf(
                            "Chest Press Machine  Машина прес за гърди",
                            "Seated Row Machine  Гребане на машина",
                            "Lat Pulldown  Лат пулдаун",
                            "Cable Lateral Raise  Странично вдигане на скрипец",
                            "Cable Biceps Curl  Сгъване за бицепс на скрипец",
                            "Cable Triceps Pushdown  Разгъване за трицепс на скрипец"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 2 - Lower",
                        exercises = listOf(
                            "Leg Press  Лег преса",
                            "Bulgarian Split Squat  Български клек",
                            "Dumbbell Reverse Lunge  Обратен напад с дъмбели",
                            "Leg Extension  Разгъване за квадрицепс на машина",
                            "Leg Curl  Сгъване за задно бедро на машина",
                            "Plank  Планк"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 3 - Full Body",
                        exercises = listOf(
                            "Hack Squat  Хак клек на машина",
                            "Leg Extension  Разгъване за квадрицепс на машина",
                            "Leg Curl  Сгъване за задно бедро на машина",
                            "Romanian Deadlift  Румънска тяга",
                            "Incline Dumbbell Press  Горна лежанка с дъмбели",
                            "Lat Pulldown  Лат пулдаун",
                            "Standing Calf Raise  Повдигане на пръсти в стоеж",
                            "Cable Lateral Raise  Странично вдигане на скрипец",
                            "Machine Crunch  Коремни преси на машина"
                        )
                    )
                )
            ),

            WorkoutProgram(
                id = "two_day_full_body",
                name = "2 Day Full Body",
                days = listOf(
                    WorkoutDay(
                        name = "Day 1 - Full Body",
                        exercises = listOf(
                            "Smith Machine Squat",
                            "Leg Press",
                            "Leg Extension",
                            "Leg Curl",
                            "Adductor",
                            "Lat Pulldown",
                            "Chest Supported Row",
                            "Pec Deck",
                            "Shoulder Press",
                            "Cable Bicep Curl",
                            "Dumbbell Skull Crushers",
                            "Ab Crunch Machine"
                        )
                    ),
                    WorkoutDay(
                        name = "Day 2 - Full Body",
                        exercises = listOf(
                            "Incline DB Press",
                            "Machine Chest Press",
                            "Machine Lateral Raise",
                            "Rear Delt Fly",
                            "Lat Prayer",
                            "Incline Curl",
                            "Tricep Pushdown",
                            "RDL",
                            "Bulgarian Split Squat",
                            "Leg Extension",
                            "Leg Curl",
                            "Plank"
                        )
                    )
                )
            )
        )
    }


}