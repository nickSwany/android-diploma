package ru.practicum.android.diploma.search.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.filters.domain.FiltersSharedInteractor
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.FiltersParameters
import ru.practicum.android.diploma.filters.domain.models.FiltersState
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.model.Vacancies
import ru.practicum.android.diploma.search.domain.model.Vacancy
import ru.practicum.android.diploma.search.domain.model.fields.Area
import ru.practicum.android.diploma.search.domain.model.fields.Industry

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val filtersSharedInteractor: FiltersSharedInteractor
) : ViewModel() {

    private var previousRequest: String = ""

    //    private var unprocessedRequest: String = "_"
    private var searchResultsList = ArrayList<Vacancy>()
    private var currentPage: Int = 0
    private var maxPages: Int = 0
    private var currentFilters = getFilters()
    private var isNextPageLoading: Boolean = false
    val searchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request ->
        if (request != previousRequest && !isNextPageLoading) {
            search(request)
        }
    }

    private val screenState = MutableLiveData<SearchScreenState>(SearchScreenState.Default)
    fun getScreenState(): LiveData<SearchScreenState> = screenState

    private val filtersState = MutableLiveData<FiltersState>(FiltersState.Inactive)
    fun getFiltersState(): LiveData<FiltersState> = filtersState

    init {
        screenState.postValue(SearchScreenState.Default)
        processFiltersStatus(getFilters())
    }

    fun search(request: String, page: Int = 0) {
        if (request != previousRequest) {
            searchResultsList.clear()
            currentPage = 0
            maxPages = 0
            screenState.postValue(SearchScreenState.Loading)
        }

        isNextPageLoading = true
        if (!request.isNullOrEmpty()) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    searchInteractor
                        .getVacancies(
                            request,
                            page,
                            FiltersParameters(
                                currentFilters.salary,
                                currentFilters.salaryFlag,
                                processIndustry(currentFilters.industry),
                                processArea(
                                    currentFilters.country,
                                    currentFilters.region
                                )
                            )
                        )
                        .catch { exception ->
                            screenState.postValue(SearchScreenState.Error)
                            isNextPageLoading = false
                        }
                        .collect { pair ->
                            processResults(pair.data, pair.message, request)
                        }
                }
            }
        }
    }

    private fun processResults(vacancies: Vacancies?, errorCode: Int?, searchRequest: String) {
        if (vacancies != null) {
            previousRequest = searchRequest
            if (vacancies.vacancies.isNotEmpty()) {
                searchResultsList.addAll(vacancies.vacancies)
                currentPage = vacancies.page
                maxPages = vacancies.pages
                Log.d(TAG_SEARCH, "Max pages: $maxPages")
                screenState.postValue(SearchScreenState.ShowContent(searchResultsList, vacancies.found))
                isNextPageLoading = false
            } else {
                screenState.postValue(SearchScreenState.SearchError)
                isNextPageLoading = false
            }
        } else {
            when (errorCode) {
                ERROR_NO_INTERNET -> {
                    screenState.postValue(SearchScreenState.InternetConnectionError)
                    isNextPageLoading = false
                }

                IO_EXCEPTION -> {
                    screenState.postValue(SearchScreenState.IOError)
                    isNextPageLoading = false
                }

                else -> {
                    screenState.postValue(SearchScreenState.ServerError)
                    isNextPageLoading = false
                }
            }
        }
    }

    fun uploadPage() {
        if (previousRequest.isNotEmpty() && !isNextPageLoading && currentPage != maxPages - ONE) {
            isNextPageLoading = true
            screenState.postValue(SearchScreenState.UploadNextPage)
            search(previousRequest, currentPage + ONE)
        }
    }

    fun clearSearchField() {
        previousRequest = ""
        searchDebounce("")
        searchResultsList.clear()
        screenState.postValue(SearchScreenState.Default)
    }

    private fun getFilters(): Filters {
        return Filters(
            filtersSharedInteractor.getSalary(),
            filtersSharedInteractor.getSalaryFlag() ?: false,
            filtersSharedInteractor.getCountry(),
            filtersSharedInteractor.getRegion(),
            filtersSharedInteractor.getIndustry()
        )
    }

    private fun processFiltersStatus(filters: Filters) {
        if (filters.salary != null ||
            filters.salaryFlag ||
            filters.country != null ||
            filters.region != null ||
            filters.industry != null
        ) {
            filtersState.postValue(FiltersState.Active)
        } else {
            filtersState.postValue(FiltersState.Inactive)
        }
    }

    fun checkFiltersStatus() {
        val filters = getFilters()
        if (filters != currentFilters) {
            currentFilters = filters
            processFiltersStatus(currentFilters)
            repeatRequest()
        }
    }

    private fun repeatRequest() {
        searchResultsList.clear()
        search(previousRequest, currentPage)
    }

    private fun processArea(country: Area?, region: Area?): String? {
        val result: String?
        result = if (country == null && region == null) {
            null
        } else if (country != null && region == null) {
            country.id.toString()
        } else {
            region?.id.toString()
        }
        return result
    }

    private fun processIndustry(industry: Industry?): String? {
        return if (industry == null) {
            null
        } else {
            industry.id.toString()
        }
    }

    companion object {
        private const val ERROR_NO_INTERNET = -1
        private const val IO_EXCEPTION = -2
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val ONE = 1
        private const val TAG_SEARCH = "SEARCH RESPONSE"
    }
}
