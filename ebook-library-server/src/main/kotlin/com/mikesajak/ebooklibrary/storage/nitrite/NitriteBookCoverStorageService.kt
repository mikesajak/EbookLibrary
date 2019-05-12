package com.mikesajak.ebooklibrary.storage.nitrite

import com.mikesajak.ebooklibrary.payload.BookCover
import com.mikesajak.ebooklibrary.payload.BookId
import com.mikesajak.ebooklibrary.storage.BookCoverStorageService
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.springframework.stereotype.Service

@Service
class NitriteBookCoverStorageService(nitriteDbService: NitriteDbService) : BookCoverStorageService {
    private val bookCoverRepo: ObjectRepository<BookCover>

    init {
        bookCoverRepo = nitriteDbService.db.getRepository(BookCover::class.java)

        if (!bookCoverRepo.hasIndex("bookId.value")) {
            bookCoverRepo.createIndex("bookId.value", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun storeCover(cover: BookCover) {
//        bookCoverRepo.update(cover, true)
        bookCoverRepo.remove(ObjectFilters.eq("bookId.value", cover.bookId.value))
        bookCoverRepo.insert(cover)

    }

    override fun getCover(bookId: BookId): BookCover? {
        val cursor = bookCoverRepo.find(ObjectFilters.eq("bookId.value", bookId.value))
        return cursor.singleOrNull()
    }

    override fun listCovers(): List<BookId> {
        return bookCoverRepo.find()
            .map { it.bookId }
    }

    override fun deleteCover(bookId: BookId): Boolean {
        return bookCoverRepo.remove(ObjectFilters.eq("bookId.value", bookId.value))
            .affectedCount > 0
    }
}
