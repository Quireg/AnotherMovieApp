package ua.in.quireg.anothermovieapp.interfaces;


public interface FetchMoreItemsCallback {
    void setPageNumber(long pageNumber);

    void setTotalPages(long totalPages);

    void setTotalResults(long totalResults);

    void fetchStarted();

    void fetchCompleted();

    void fetchErrorOccured();
}
