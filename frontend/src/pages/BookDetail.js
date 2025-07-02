import React, { useEffect, useState } from "react";
import { useParams} from "react-router-dom";
import AppHeader from "../components/AppHeader";

export default function BookDetail(isLoggedIn = false, isAuthor = false) {
    const { id } = useParams();
    const [book, setBook] = useState(null);

    useEffect(() => {
        // TODO: 실제 API 호출로 교체
        const fetchBookDetail = async () => {
            // 더미 데이터 (실제 앱에서는 API로 받아와야 함)
            const dummyBook = {
                id,
                title: `책 제목 ${id}`,
                authorName: `저자 ${id}`,
                summary: "이 책은 ...",
                category: "소설",
                coverImageUrl: "https://via.placeholder.com/150",
                ebookUrl: "https://example.com/ebook.pdf",
                price: 12000,
                viewCount: 42,
            };
            setBook(dummyBook);
        };

        fetchBookDetail();
    }, [id]);

    if (!book) return <div className="p-8">로딩 중...</div>;

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={isLoggedIn} isAuthor={isAuthor} />
            <main className="container mx-auto px-6 py-10 max-w-4xl">
                <div className="flex flex-col md:flex-row gap-8">
                    <img
                        src={book.coverImageUrl}
                        alt="표지 이미지"
                        className="w-48 h-64 object-cover border rounded shadow"
                    />

                    <div className="flex flex-col gap-2">
                        <h1 className="text-2xl font-bold">{book.title}</h1>
                        <p className="text-gray-700">저자: {book.authorName}</p>
                        <p className="text-gray-600">카테고리: {book.category}</p>
                        <p className="text-gray-500">조회수: {book.viewCount}</p>
                        <p className="text-gray-900 font-semibold mt-2">가격: {book.price.toLocaleString()}원</p>
                        <p className="mt-4 whitespace-pre-wrap">{book.summary}</p>
                        <a
                            href={book.ebookUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="mt-6 inline-block text-blue-600 underline"
                        >
                            전자책 보기
                        </a>
                    </div>
                </div>
            </main>
        </div>
    );
}
