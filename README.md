# Приложение “Поиск работы” 
## Описание
Дипломный проект представляет собой небольшое приложение для поиска работы,
использующее [API сервиса HeadHunter](https://github.com/hhru/api). Приложение предоставляет следующую функциональность:
- Поиск вакансий;
- Указание фильтров для поиска;
- Просмотр деталей отдельной вакансии;
- И добавление вакансий в список "Избранного".
## Скриншоты
<p align="center">
   <img src="https://github.com/nickSwany/android-diploma/blob/455f45e44d006dd76dfeb909c411111acd267120/app/src/main/res/screenschots/favoriteVacancy.jpg" alt="screenshot1" width="200"/>
   <img src="https://github.com/nickSwany/android-diploma/blob/455f45e44d006dd76dfeb909c411111acd267120/app/src/main/res/screenschots/filter.jpg" alt="screenshot1" width="200"/>
   <img src="https://github.com/nickSwany/android-diploma/blob/455f45e44d006dd76dfeb909c411111acd267120/app/src/main/res/screenschots/info_vacancy.jpg" alt="screenshot1" width="200"/>
   <img src="https://github.com/nickSwany/android-diploma/blob/455f45e44d006dd76dfeb909c411111acd267120/app/src/main/res/screenschots/search.jpg" alt="screenshot1" width="200"/>
</p>

## Технологии и подходы, использованные в проекте
- **Single Activity Architecture**: Изначально работал с несколькими Activity, затем перешёл на архитектурный подход Single Activity для улучшения навигации и управления состоянием приложения.
- **Retrofit**: Использовал Retrofit для взаимодействия с iTunes API.
- **Clean Architecture**: Применял принципы чистой архитектуры для разделения приложения на слои.
- **MVP и MVVM**: Изначально использовал паттерн MVP, затем переписал приложение на MVVM для улучшения поддерживаемости и тестируемости кода.
- **Coroutines и Flow**: Реализовал асинхронные операции с использованием Coroutines и Flow для эффективного управления потоками данных.
- **Koin**: Внедрение зависимостей с помощью Koin для упрощения управления зависимостями.

## Статический анализ

В проекте настроен базовый статический анализатор - [detekt](https://detekt.dev/).

## Настройка Github Actions

В дипломном проекте используется сервис [Github Actions](https://github.com/features/actions) для настройки CI (
Continuous Integration). Это позволяет автоматизировать базовые проверки качества приложения, такие как компиляция
проекта и прогон статического анализатора [detekt](https://github.com/detekt/detekt). Файл конфигурации CI вы
можете [найти здесь](./.github/workflows/pr_checks.yml).

## Полученный опыт
Приобрел опыт работы в команде разработчиков, проведение код-ревью. работал с API HeadHunter, реализовал список поиска и избранного, просмотр деталей вакансии, использовал GitHub Project для управления задачами, в работе пользовался GitHub Actions.

## Библиотеки
- Room
- Kotlin Coroutines
- Navigation
- Fragment KTX
- Material Component
- Glide
- Gson
- Retrofit
- Koin
- Lifecycle ViewModel KTX
- AppCompat
- ConstraintLayout

## Требования
- Приложение должно поддерживать устройства, начиная с Android 8.0 (minSdkVersion = 26)
- **IDE**: Android Studio Koala
- **JDK**: Java 17
- **Kotlin**: 1.7.10
