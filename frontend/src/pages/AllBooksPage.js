import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
import AppHeader from "../components/AppHeader";

export default function AllBooksPage({ isLoggedIn = false, isAuthor = false }) {
    const navigate = useNavigate();

    const [books, setBooks] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const API_BASE = process.env.REACT_APP_API_URL;

    // 📡 fetch 사용
    const fetchBooks = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_BASE}/books?page=0&size=200`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            setBooks(data.content);
        } catch (err) {
            setError(err.message || "네트워크 오류");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBooks();
    }, []);

    const filteredBooks = books.filter((b) =>
        b.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={isLoggedIn} isAuthor={isAuthor} />

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📚 전체 책 목록</h2>

                <div className="flex justify-between items-center mb-6">
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder="책 제목 검색"
                        className="border px-4 py-2 rounded-md w-full max-w-xs"
                    />

                    {isAuthor && (
                        <Button className="ml-4" onClick={() => navigate("/manuscript")}>
                            원고 등록
                        </Button>
                    )}
                </div>

                {loading && <p>로딩 중...</p>}
                {error && <p className="text-red-500">{error}</p>}
                {!loading && !error && filteredBooks.length === 0 && <p>도서가 없습니다.</p>}

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                    {filteredBooks.map((book) => (
                        <div
                            key={book.id}
                            className="bg-white border shadow rounded-md p-4 hover:shadow-md cursor-pointer"
                            onClick={() => navigate(`/book/${book.id}`)}
                        >
                            <div
                                className="w-full h-40 bg-gray-200 mb-2"
                                style={{
                                    backgroundImage: `url(${book.coverImageUrl})`,
                                    backgroundSize: "cover",
                                    backgroundPosition: "center",
                                }}
                            />
                            <div className="text-sm font-semibold text-center">{book.title}</div>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    );
}
