/**
 * ロールを単位とした簡易な認可機能を提供するパッケージ。
 * <p>
 * {@code nablarch.common.permission}が、Permission(操作)を単位とした詳細な認可機能を
 * 提供しているのに対して、本パッケージが提供する認可機能はロール(役割)を単位としている。
 * </p>
 * <p>
 * 本パッケージが提供する認可機能はインターセプタの仕組みを利用しており、
 * アノテーションベースの実装が前提となっている。
 * </p>
 */
package nablarch.common.authorization.role;