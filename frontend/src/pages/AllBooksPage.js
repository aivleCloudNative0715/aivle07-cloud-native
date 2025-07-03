import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext";

export default function AllBooksPage() {
    const navigate = useNavigate();
    const { user, isLoggedIn } = useAuth();
    const userId = user?.userId ?? 0;

    const API_BASE = process.env.REACT_APP_API_URL;

    /* ───────── state ───────── */
    const [books, setBooks] = useState([]);
    const [viewedBookIds, setViewedBookIds] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [showReadOnly, setShowReadOnly] = useState(false);   // ← 하나만 사용
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /* ───────── effect ───────── */
    useEffect(() => {
        fetchBooks();
        if (userId) fetchViewedBooks(userId);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [userId]);

    const fetchBooks = async () => {
        setLoading(true);
        setError(null);
        try {
            const res = await fetch(`${API_BASE}/books?page=0&size=200`);
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            const data = await res.json();
            setBooks(data.content);
        } catch (err) {
            setError(err.message || "네트워크 오류");
        } finally {
            setLoading(false);
        }
    };

    const fetchViewedBooks = async (uid) => {
        try {
            const res = await fetch(`${API_BASE}/bookViews/users/${uid}`);
            if (res.ok) {
                const data = await res.json();          // [{ bookId, ... }]
                setViewedBookIds(data.map(v => v.bookId));
            }
        } catch (err) {
            console.error("열람 기록 조회 실패:", err);
        }
    };

    /* ───────── filter ───────── */
    const filteredBooks = books.filter(b => {
        const matches = b.title.toLowerCase().includes(searchQuery.toLowerCase());
        if (!matches) return false;
        if (!showReadOnly) return true;             // 전체 보기
        return viewedBookIds.includes(b.id);        // 읽은 책만
    });

    /* ───────── UI ───────── */
    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📚 전체 책 목록</h2>

                {/* 검색 · 필터 · 원고등록 */}
                <div className="flex justify-between items-center mb-6">
                    <div className="flex items-center gap-2">
                        <input
                            value={searchQuery}
                            onChange={e => setSearchQuery(e.target.value)}
                            placeholder="책 제목 검색"
                            className="border px-4 py-2 rounded-md w-60"
                        />
                        {isLoggedIn && (
                            <button
                                onClick={() => setShowReadOnly(prev => !prev)}
                                className={`px-3 py-2 rounded-md text-sm font-medium border transition
                  ${showReadOnly
                                    ? "bg-blue-600 text-white hover:bg-blue-700 border-blue-600"
                                    : "bg-white text-gray-700 hover:bg-gray-100 border-gray-300"}
                `}
                            >
                                {showReadOnly ? "📘 읽은 책만 보기" : "📚 전체 보기"}
                            </button>
                        )}
                    </div>
                </div>

                {/* 목록 or 메시지 */}
                {loading && <p>로딩 중...</p>}
                {error && <p className="text-red-500">{error}</p>}

                {!loading && !error && filteredBooks.length === 0 && (
                    <p className="text-gray-500 text-center">
                        {showReadOnly
                            ? "읽은 책이 없습니다."
                            : "조건에 맞는 도서가 없습니다."}
                    </p>
                )}

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                    {filteredBooks.map(book => (
                        <div
                            key={book.id}
                            onClick={() => navigate(`/book/${book.id}`)}
                            className="bg-white border shadow rounded-md p-4 hover:shadow-md cursor-pointer"
                        >
                            <div
                                className="w-full h-40 bg-gray-200 mb-2"
                                style={{
                                    backgroundImage: `url(${book.coverImageUrl})`,
                                    backgroundSize: "cover",
                                    backgroundPosition: "center",
                                }}
                            />
                            <div className="text-sm font-semibold text-center">
                                {book.title}
                            </div>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    );
}
