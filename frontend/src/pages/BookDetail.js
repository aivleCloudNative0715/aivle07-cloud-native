// src/pages/BookDetail.jsx
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext";

export default function BookDetail() {
    const { id } = useParams();                     // 📕 현재 조회 중인 도서 ID (path param)
    const { user }   = useAuth();                   // 🔑 로그인 정보
    const isLoggedIn = !!user;
    const userId     = user?.userId ?? 0;

    const API_BASE = process.env.REACT_APP_API_URL;

    /* ──────────────── state ──────────────── */
    const [book,        setBook]        = useState(null);
    const [loading,     setLoading]     = useState(true);
    const [error,       setError]       = useState(null);
    const [hasAccess,   setHasAccess]   = useState(false);
    const [isRequesting,setIsRequesting]= useState(false);
    const [requestMsg,  setRequestMsg]  = useState(null);

    /* ──────────────── effect: 도서 + 열람권한 ──────────────── */
    useEffect(() => {
        const controller = new AbortController();

        const fetchBookAndAccess = async () => {
            try {
                /* 1) 도서 상세 */
                const bookRes = await fetch(`${API_BASE}/books/${id}`, { signal: controller.signal });
                if (!bookRes.ok) throw new Error("도서 정보를 불러오는 데 실패했습니다.");
                const bookData = await bookRes.json();
                setBook(bookData);

                /* 2) 열람 기록 (로그인한 사용자인 경우에만) */
                if (userId) {
                    const viewRes = await fetch(`${API_BASE}/bookViews/users/${userId}`, { signal: controller.signal });
                    if (viewRes.ok) {
                        const views = await viewRes.json();            // [{bookId, …}, …]
                        const viewed = views.some(v => v.bookId === Number(id));
                        setHasAccess(viewed);
                    }
                }
            } catch (err) {
                if (err.name !== "AbortError") {
                    setError(err.message || "알 수 없는 오류가 발생했습니다.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchBookAndAccess();
        return () => controller.abort();
    }, [id, API_BASE, userId]);

    const handleAccessRequest = async () => {
        if (!isLoggedIn || isRequesting) return;

        setIsRequesting(true);
        setRequestMsg(null);

        try {
            const res = await fetch(`${API_BASE}/users/request-content-access`, {
                method : "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${user.token}`,
                },
                body: JSON.stringify({ userId, bookId: Number(id) }),
            });

            if (!res.ok) throw new Error();

            setRequestMsg("✅ 열람 신청이 완료되었습니다!");

            setTimeout(() => window.location.reload(), 3000);
        } catch {
            setRequestMsg("❌ 열람 신청 중 오류가 발생했습니다.");
            setIsRequesting(false);
        }
    };

    if (loading)  return <div className="p-8">로딩 중...</div>;
    if (error)    return <div className="p-8 text-red-500">{error}</div>;
    if (!book)    return <div className="p-8">도서를 찾을 수 없습니다.</div>;

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />

            <main className="container mx-auto px-6 py-10 max-w-4xl">
                <div className="flex flex-col md:flex-row gap-8">
                    {/* ───── 표지 ───── */}
                    <img
                        src={book.coverImageUrl}
                        alt="표지 이미지"
                        className="w-48 h-64 object-cover border rounded shadow"
                    />

                    {/* ───── 메타 정보 & 콘텐츠 ───── */}
                    <div className="flex flex-col gap-2">
                        <h1 className="text-2xl font-bold">{book.title}</h1>
                        <p className="text-gray-700">저자: {book.authorName}</p>
                        <p className="text-gray-600">카테고리: {book.category}</p>
                        <p className="text-gray-500">조회수: {book.viewCount}</p>
                        <p className="text-gray-900 font-semibold mt-2">
                            가격: {book.price?.toLocaleString()}원
                        </p>

                        {/* ───── 열람 권한 有 ───── */}
                        {hasAccess ? (
                            <>
                                <p>내용:</p>
                                <div style={{ whiteSpace: 'pre-wrap', padding: '1rem', border: '1px solid #ccc', borderRadius: '8px', lineHeight: '1.6' }}>

                                    {book.content}
                                </div>
                            </>
                        ) : (
                            /* ───── 열람 권한 無 ───── */
                            <>

                                <p className="mt-4 whitespace-pre-wrap">요약: {book.summary}</p>
                                <p className="mt-4 text-sm text-gray-500">
                                    ※ 열람 권한이 있는 사용자만 내용을 볼 수 있습니다.
                                </p>

                                {isLoggedIn && (
                                    <button
                                        onClick={handleAccessRequest}
                                        disabled={isRequesting}
                                        className={`mt-4 px-4 py-2 rounded text-white transition ${
                                            isRequesting
                                                ? "bg-gray-400 cursor-not-allowed"
                                                : "bg-blue-600 hover:bg-blue-700"
                                        }`}
                                    >
                                        {isRequesting ? "신청 중..." : "열람 신청"}
                                    </button>
                                )}

                                {requestMsg && (
                                    <p className="mt-2 text-sm">{requestMsg}</p>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}
