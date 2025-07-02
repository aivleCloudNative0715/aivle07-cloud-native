import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import AppHeader from "../components/AppHeader";
import {useAuth} from "../context/AuthContext";

export default function BookDetail() {
    const { id } = useParams();
    const { user } = useAuth();
    const isLoggedIn = !!user;
    const userId = user?.userId || 0;
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [requestStatus, setRequestStatus] = useState(null);
    const [hasViewed, setHasViewed] = useState(false);
    const [isRequesting, setIsRequesting] = useState(false);

    const API_BASE = process.env.REACT_APP_API_URL;

    useEffect(() => {
        const fetchBookDetail = async () => {
            try {
                const res = await fetch(`${API_BASE}/books/${id}`);
                if (!res.ok) throw new Error("도서 정보를 불러오는 데 실패했습니다.");
                const data = await res.json();
                setBook(data);
            } catch (err) {
                setError(err.message || "알 수 없는 오류");
            } finally {
                setLoading(false);
            }
        };

        const fetchViewRecord = async () => {
            if (!userId) return;
            const res = await fetch(`${API_BASE}/bookViews/users/${userId}`);
            if (res.ok) {
                const views = await res.json();
                const hasViewed = views.some(view => view.bookId === parseInt(id));
                setHasViewed(hasViewed);
            }
        };

        fetchBookDetail();
        fetchViewRecord();
    }, [id, API_BASE, userId]);

    const handleAccessRequest = async () => {
        try {
            setRequestStatus(null);
            setIsRequesting(true);

            const res = await fetch(`${API_BASE}/users/request-content-access`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${user.token}`,
                },
                body: JSON.stringify({
                    userId,
                    bookId: parseInt(id),
                }),
            });

            if (!res.ok) throw new Error("열람 신청에 실패했습니다.");
            setRequestStatus("✅ 열람 신청이 완료되었습니다.");

            setTimeout(() => {
                window.location.reload();
            }, 3000);
        } catch (err) {
            setRequestStatus("❌ 열람 신청 중 오류가 발생했습니다.");
            setIsRequesting(false);
        }
    };



    if (loading) return <div className="p-8">로딩 중...</div>;
    if (error) return <div className="p-8 text-red-500">{error}</div>;
    if (!book) return <div className="p-8">도서를 찾을 수 없습니다.</div>;

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />
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
                        <p className="text-gray-900 font-semibold mt-2">
                            가격: {book.price?.toLocaleString()}원
                        </p>
                        <p className="mt-4 whitespace-pre-wrap">요약: {book.summary}</p>
                        <a
                            href={book.ebookUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="mt-6 inline-block text-blue-600 underline"
                        >
                            전자책 보기
                        </a>

                        {/* 열람 신청 */}
                        {/* 열람 신청 */}
                        {isLoggedIn && !hasViewed && (
                            <button
                                onClick={handleAccessRequest}
                                disabled={isRequesting}
                                className={`mt-6 px-4 py-2 rounded text-white transition ${
                                    isRequesting ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'
                                }`}
                            >
                                {isRequesting ? '신청 중...' : '열람 신청'}
                            </button>
                        )}
                        {requestStatus && <p className="mt-2 text-sm">{requestStatus}</p>}

                    </div>
                </div>
            </main>
        </div>
    );
}
