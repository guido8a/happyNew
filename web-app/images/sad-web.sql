--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.11
-- Dumped by pg_dump version 9.3.1
-- Started on 2014-03-19 15:15:35

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 221 (class 3079 OID 11676)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2268 (class 0 OID 0)
-- Dependencies: 221
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 161 (class 1259 OID 103963)
-- Name: accn; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE accn (
    accn__id integer NOT NULL,
    ctrl__id integer NOT NULL,
    mdlo__id integer NOT NULL,
    tpac__id integer NOT NULL,
    accnnmbr character varying(63) NOT NULL,
    accndscr character varying(63) NOT NULL,
    accnaudt smallint NOT NULL
);


ALTER TABLE public.accn OWNER TO postgres;

--
-- TOC entry 162 (class 1259 OID 103966)
-- Name: accn_accn__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE accn_accn__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.accn_accn__id_seq OWNER TO postgres;

--
-- TOC entry 2269 (class 0 OID 0)
-- Dependencies: 162
-- Name: accn_accn__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE accn_accn__id_seq OWNED BY accn.accn__id;


--
-- TOC entry 163 (class 1259 OID 103968)
-- Name: accs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE accs (
    accs__id integer NOT NULL,
    prsn__id integer,
    accsfcin date,
    accsfcfn date,
    accsobsr character varying(127),
    usro__id bigint NOT NULL,
    prsnasgn bigint NOT NULL
);


ALTER TABLE public.accs OWNER TO postgres;

--
-- TOC entry 2270 (class 0 OID 0)
-- Dependencies: 163
-- Name: TABLE accs; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE accs IS 'acceso';


--
-- TOC entry 164 (class 1259 OID 103971)
-- Name: accs_accs__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE accs_accs__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.accs_accs__id_seq OWNER TO postgres;

--
-- TOC entry 2271 (class 0 OID 0)
-- Dependencies: 164
-- Name: accs_accs__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE accs_accs__id_seq OWNED BY accs.accs__id;


--
-- TOC entry 165 (class 1259 OID 103973)
-- Name: anio; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE anio (
    anio__id integer NOT NULL,
    anionmro character(4) NOT NULL
);


ALTER TABLE public.anio OWNER TO postgres;

--
-- TOC entry 2272 (class 0 OID 0)
-- Dependencies: 165
-- Name: TABLE anio; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE anio IS 'Año de Proceso';


--
-- TOC entry 166 (class 1259 OID 103976)
-- Name: anio_anio__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE anio_anio__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.anio_anio__id_seq OWNER TO postgres;

--
-- TOC entry 2273 (class 0 OID 0)
-- Dependencies: 166
-- Name: anio_anio__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE anio_anio__id_seq OWNED BY anio.anio__id;


--
-- TOC entry 167 (class 1259 OID 103978)
-- Name: audt; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE audt (
    audt__id bigint NOT NULL,
    audtaccn character varying(255) NOT NULL,
    audtcmpo character varying(255),
    audtctrl character varying(255) NOT NULL,
    audttbla character varying(255) NOT NULL,
    audtfcha timestamp without time zone,
    audtnewv character varying(255),
    audtoldv character varying(255),
    audtoprc character varying(255) NOT NULL,
    prfl__id bigint NOT NULL,
    reg_id integer NOT NULL,
    usro__id bigint NOT NULL
);


ALTER TABLE public.audt OWNER TO postgres;

--
-- TOC entry 168 (class 1259 OID 103984)
-- Name: audt_audt__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE audt_audt__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audt_audt__id_seq OWNER TO postgres;

--
-- TOC entry 2274 (class 0 OID 0)
-- Dependencies: 168
-- Name: audt_audt__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE audt_audt__id_seq OWNED BY audt.audt__id;


--
-- TOC entry 169 (class 1259 OID 103986)
-- Name: ctrl; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ctrl (
    ctrl__id integer NOT NULL,
    ctrlnmbr character varying(63) NOT NULL
);


ALTER TABLE public.ctrl OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 103989)
-- Name: ctrl_ctrl__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ctrl_ctrl__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ctrl_ctrl__id_seq OWNER TO postgres;

--
-- TOC entry 2275 (class 0 OID 0)
-- Dependencies: 170
-- Name: ctrl_ctrl__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ctrl_ctrl__id_seq OWNED BY ctrl.ctrl__id;


--
-- TOC entry 171 (class 1259 OID 103991)
-- Name: dctr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dctr (
    dctr__id integer NOT NULL,
    trmt__id integer,
    trmtanxo integer,
    dctrfcha date,
    dctrrsmn character varying(1024),
    dctrclve character varying(63),
    dcmtpath character varying(1024),
    dctrdscr character varying(63),
    dctrfcrv timestamp without time zone
);


ALTER TABLE public.dctr OWNER TO postgres;

--
-- TOC entry 2276 (class 0 OID 0)
-- Dependencies: 171
-- Name: TABLE dctr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE dctr IS 'Documento del Trámite';


--
-- TOC entry 172 (class 1259 OID 103997)
-- Name: dctr_dctr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE dctr_dctr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dctr_dctr__id_seq OWNER TO postgres;

--
-- TOC entry 2277 (class 0 OID 0)
-- Dependencies: 172
-- Name: dctr_dctr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE dctr_dctr__id_seq OWNED BY dctr.dctr__id;


--
-- TOC entry 173 (class 1259 OID 103999)
-- Name: ddlb; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ddlb (
    ddlb__id bigint NOT NULL,
    ddlbanio integer NOT NULL,
    ddlbddia character varying(3) NOT NULL,
    ddlbfcha timestamp without time zone NOT NULL,
    ddlbobsr character varying(511),
    ddlbordn integer NOT NULL
);


ALTER TABLE public.ddlb OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 104005)
-- Name: ddlb_ddlb__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ddlb_ddlb__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ddlb_ddlb__id_seq OWNER TO postgres;

--
-- TOC entry 2278 (class 0 OID 0)
-- Dependencies: 174
-- Name: ddlb_ddlb__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ddlb_ddlb__id_seq OWNED BY ddlb.ddlb__id;


--
-- TOC entry 175 (class 1259 OID 104007)
-- Name: dpto; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dpto (
    dpto__id integer NOT NULL,
    tpdp__id integer,
    dptopdre integer,
    dptocdgo character varying(6) NOT NULL,
    dptodscr character varying(63) NOT NULL,
    dptotelf character varying(15),
    dptoextn character varying(7),
    dptodire character varying(255),
    dptoetdo character varying(1)
);


ALTER TABLE public.dpto OWNER TO postgres;

--
-- TOC entry 2279 (class 0 OID 0)
-- Dependencies: 175
-- Name: TABLE dpto; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE dpto IS 'Departamento Personal';


--
-- TOC entry 176 (class 1259 OID 104010)
-- Name: dpto_dpto__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE dpto_dpto__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dpto_dpto__id_seq OWNER TO postgres;

--
-- TOC entry 2280 (class 0 OID 0)
-- Dependencies: 176
-- Name: dpto_dpto__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE dpto_dpto__id_seq OWNED BY dpto.dpto__id;


--
-- TOC entry 177 (class 1259 OID 104012)
-- Name: edtr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE edtr (
    edtr__id integer NOT NULL,
    edtrcdgo character varying(4) NOT NULL,
    edtrdscr character varying(31) NOT NULL
);


ALTER TABLE public.edtr OWNER TO postgres;

--
-- TOC entry 2281 (class 0 OID 0)
-- Dependencies: 177
-- Name: TABLE edtr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE edtr IS 'Estado del trámite';


--
-- TOC entry 178 (class 1259 OID 104015)
-- Name: edtr_edtr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE edtr_edtr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.edtr_edtr__id_seq OWNER TO postgres;

--
-- TOC entry 2282 (class 0 OID 0)
-- Dependencies: 178
-- Name: edtr_edtr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE edtr_edtr__id_seq OWNED BY edtr.edtr__id;


--
-- TOC entry 179 (class 1259 OID 104017)
-- Name: logf; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE logf (
    logf__id bigint NOT NULL,
    logfcaus character varying(255) NOT NULL,
    logferro character varying(255) NOT NULL,
    logffcha timestamp without time zone NOT NULL,
    logf_url character varying(255) NOT NULL,
    logfusro bigint NOT NULL
);


ALTER TABLE public.logf OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 104023)
-- Name: logf_logf__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE logf_logf__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.logf_logf__id_seq OWNER TO postgres;

--
-- TOC entry 2283 (class 0 OID 0)
-- Dependencies: 180
-- Name: logf_logf__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE logf_logf__id_seq OWNED BY logf.logf__id;


--
-- TOC entry 181 (class 1259 OID 104025)
-- Name: mdlo; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mdlo (
    mdlo__id integer NOT NULL,
    mdlonmbr character varying(31) NOT NULL,
    mdlodscr character varying(63) NOT NULL,
    mdloordn smallint NOT NULL
);


ALTER TABLE public.mdlo OWNER TO postgres;

--
-- TOC entry 2284 (class 0 OID 0)
-- Dependencies: 181
-- Name: TABLE mdlo; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE mdlo IS 'módulo del sistema';


--
-- TOC entry 182 (class 1259 OID 104028)
-- Name: mdlo_mdlo__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mdlo_mdlo__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mdlo_mdlo__id_seq OWNER TO postgres;

--
-- TOC entry 2285 (class 0 OID 0)
-- Dependencies: 182
-- Name: mdlo_mdlo__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mdlo_mdlo__id_seq OWNED BY mdlo.mdlo__id;


--
-- TOC entry 183 (class 1259 OID 104030)
-- Name: nmro; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE nmro (
    nmro__id integer NOT NULL,
    dpto__id integer,
    tpdc__id integer,
    nmrovlor smallint NOT NULL
);


ALTER TABLE public.nmro OWNER TO postgres;

--
-- TOC entry 2286 (class 0 OID 0)
-- Dependencies: 183
-- Name: TABLE nmro; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE nmro IS 'Numeración de documentos';


--
-- TOC entry 184 (class 1259 OID 104033)
-- Name: nmro_nmro__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE nmro_nmro__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nmro_nmro__id_seq OWNER TO postgres;

--
-- TOC entry 2287 (class 0 OID 0)
-- Dependencies: 184
-- Name: nmro_nmro__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE nmro_nmro__id_seq OWNED BY nmro.nmro__id;


--
-- TOC entry 185 (class 1259 OID 104035)
-- Name: obtr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE obtr (
    obtr__id integer NOT NULL,
    trmt__id integer,
    prsn__id integer,
    obtrfcha timestamp without time zone NOT NULL,
    obtrobsr character varying(1023) NOT NULL,
    obtrtipo character varying(10)
);


ALTER TABLE public.obtr OWNER TO postgres;

--
-- TOC entry 2288 (class 0 OID 0)
-- Dependencies: 185
-- Name: TABLE obtr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE obtr IS 'Observaciones y notas al trámite';


--
-- TOC entry 186 (class 1259 OID 104041)
-- Name: obtr_obtr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE obtr_obtr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.obtr_obtr__id_seq OWNER TO postgres;

--
-- TOC entry 2289 (class 0 OID 0)
-- Dependencies: 186
-- Name: obtr_obtr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE obtr_obtr__id_seq OWNED BY obtr.obtr__id;


--
-- TOC entry 187 (class 1259 OID 104043)
-- Name: orgn; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE orgn (
    orgn__id integer NOT NULL,
    tppr__id integer,
    orgncdla character varying(13) NOT NULL,
    orgnfcha date,
    orgnnmbr character varying(127) NOT NULL,
    orgnnbct character varying(31),
    orgnapct character varying(31) NOT NULL,
    prsntitl character varying(4),
    orgncrgo character varying(127),
    orgnmail character varying(63),
    orgntelf character varying(63)
);


ALTER TABLE public.orgn OWNER TO postgres;

--
-- TOC entry 2290 (class 0 OID 0)
-- Dependencies: 187
-- Name: TABLE orgn; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE orgn IS 'Persona natural o jurídica que origina el trámite externo';


--
-- TOC entry 188 (class 1259 OID 104046)
-- Name: orgn_orgn__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE orgn_orgn__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orgn_orgn__id_seq OWNER TO postgres;

--
-- TOC entry 2291 (class 0 OID 0)
-- Dependencies: 188
-- Name: orgn_orgn__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE orgn_orgn__id_seq OWNED BY orgn.orgn__id;


--
-- TOC entry 189 (class 1259 OID 104048)
-- Name: perm; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE perm (
    perm__id integer NOT NULL,
    permcdgo character varying(4) NOT NULL,
    permdscr character varying(63) NOT NULL
);


ALTER TABLE public.perm OWNER TO postgres;

--
-- TOC entry 2292 (class 0 OID 0)
-- Dependencies: 189
-- Name: TABLE perm; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE perm IS 'Permisos';


--
-- TOC entry 190 (class 1259 OID 104051)
-- Name: perm_perm__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE perm_perm__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.perm_perm__id_seq OWNER TO postgres;

--
-- TOC entry 2293 (class 0 OID 0)
-- Dependencies: 190
-- Name: perm_perm__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE perm_perm__id_seq OWNED BY perm.perm__id;


--
-- TOC entry 191 (class 1259 OID 104053)
-- Name: prfl; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE prfl (
    prfl__id integer NOT NULL,
    prflpdre integer,
    prflnmbr character varying(63) NOT NULL,
    prfldscr character varying(63) NOT NULL,
    prflobsr character varying(63),
    prflcdgo character varying(4)
);


ALTER TABLE public.prfl OWNER TO postgres;

--
-- TOC entry 192 (class 1259 OID 104056)
-- Name: prfl_prfl__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE prfl_prfl__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prfl_prfl__id_seq OWNER TO postgres;

--
-- TOC entry 2294 (class 0 OID 0)
-- Dependencies: 192
-- Name: prfl_prfl__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE prfl_prfl__id_seq OWNED BY prfl.prfl__id;


--
-- TOC entry 193 (class 1259 OID 104058)
-- Name: prms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE prms (
    prms__id integer NOT NULL,
    prfl__id integer NOT NULL,
    accn__id integer NOT NULL
);


ALTER TABLE public.prms OWNER TO postgres;

--
-- TOC entry 194 (class 1259 OID 104061)
-- Name: prms_prms__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE prms_prms__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prms_prms__id_seq OWNER TO postgres;

--
-- TOC entry 2295 (class 0 OID 0)
-- Dependencies: 194
-- Name: prms_prms__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE prms_prms__id_seq OWNED BY prms.prms__id;


--
-- TOC entry 195 (class 1259 OID 104063)
-- Name: prsn; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE prsn (
    prsn__id integer NOT NULL,
    dpto__id integer,
    prsncdla character varying(10),
    prsnnmbr character varying(31) NOT NULL,
    prsnapll character varying(31) NOT NULL,
    prsnfcna date,
    prsnfcin date,
    prsnfcfn date,
    prsnsgla character varying(4),
    prsntitl character varying(4),
    prsncrgo character varying(127),
    prsnmail character varying(63),
    prsnlogn character varying(15),
    prsnpass character varying(63),
    prsnactv smallint NOT NULL,
    prsnatrz character varying(63),
    prsnfcps date,
    prsntelf character varying(15),
    prsnjefe smallint NOT NULL,
    prsntfcl character varying(15),
    prsnfoto character varying(255),
    prsncdgo character varying(15)
);


ALTER TABLE public.prsn OWNER TO postgres;

--
-- TOC entry 2296 (class 0 OID 0)
-- Dependencies: 195
-- Name: TABLE prsn; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE prsn IS 'Personal del GADPP Usuarios';


--
-- TOC entry 196 (class 1259 OID 104069)
-- Name: prsn_prsn__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE prsn_prsn__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prsn_prsn__id_seq OWNER TO postgres;

--
-- TOC entry 2297 (class 0 OID 0)
-- Dependencies: 196
-- Name: prsn_prsn__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE prsn_prsn__id_seq OWNED BY prsn.prsn__id;


--
-- TOC entry 197 (class 1259 OID 104071)
-- Name: prtr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE prtr (
    prtr__id integer NOT NULL,
    rltr__id integer,
    prsn__id integer,
    trmt__id integer,
    prtrobsr character varying(1023),
    prtrprms character varying(4),
    prtrfcar timestamp without time zone,
    prtrfcen timestamp without time zone,
    prtrfclr timestamp without time zone,
    prtrfcrc timestamp without time zone,
    prtrfcrs timestamp without time zone,
    dpto__id bigint
);


ALTER TABLE public.prtr OWNER TO postgres;

--
-- TOC entry 2298 (class 0 OID 0)
-- Dependencies: 197
-- Name: TABLE prtr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE prtr IS 'Permsonas que intervienen en el trámite';


--
-- TOC entry 198 (class 1259 OID 104077)
-- Name: prtr_prtr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE prtr_prtr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prtr_prtr__id_seq OWNER TO postgres;

--
-- TOC entry 2299 (class 0 OID 0)
-- Dependencies: 198
-- Name: prtr_prtr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE prtr_prtr__id_seq OWNED BY prtr.prtr__id;


--
-- TOC entry 199 (class 1259 OID 104079)
-- Name: prus; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE prus (
    prus__id integer NOT NULL,
    prsn__id integer,
    perm__id integer,
    prusfcin date NOT NULL,
    prusfcfn date,
    prusobsv character varying(100),
    prsnasgn bigint NOT NULL
);


ALTER TABLE public.prus OWNER TO postgres;

--
-- TOC entry 2300 (class 0 OID 0)
-- Dependencies: 199
-- Name: TABLE prus; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE prus IS 'Permisos del usuario';


--
-- TOC entry 200 (class 1259 OID 104082)
-- Name: prus_prus__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE prus_prus__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prus_prus__id_seq OWNER TO postgres;

--
-- TOC entry 2301 (class 0 OID 0)
-- Dependencies: 200
-- Name: prus_prus__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE prus_prus__id_seq OWNED BY prus.prus__id;


--
-- TOC entry 201 (class 1259 OID 104084)
-- Name: rltr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rltr (
    rltr__id integer NOT NULL,
    rltrcdgo character varying(4) NOT NULL,
    rltrdscr character varying(63) NOT NULL
);


ALTER TABLE public.rltr OWNER TO postgres;

--
-- TOC entry 2302 (class 0 OID 0)
-- Dependencies: 201
-- Name: TABLE rltr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE rltr IS 'Rol en el trámite';


--
-- TOC entry 202 (class 1259 OID 104087)
-- Name: rltr_rltr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE rltr_rltr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rltr_rltr__id_seq OWNER TO postgres;

--
-- TOC entry 2303 (class 0 OID 0)
-- Dependencies: 202
-- Name: rltr_rltr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE rltr_rltr__id_seq OWNED BY rltr.rltr__id;


--
-- TOC entry 203 (class 1259 OID 104089)
-- Name: sesn; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sesn (
    sesn__id integer NOT NULL,
    prfl__id integer,
    prsn__id integer
);


ALTER TABLE public.sesn OWNER TO postgres;

--
-- TOC entry 2304 (class 0 OID 0)
-- Dependencies: 203
-- Name: TABLE sesn; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE sesn IS 'sesion';


--
-- TOC entry 204 (class 1259 OID 104092)
-- Name: sesn_sesn__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sesn_sesn__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sesn_sesn__id_seq OWNER TO postgres;

--
-- TOC entry 2305 (class 0 OID 0)
-- Dependencies: 204
-- Name: sesn_sesn__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE sesn_sesn__id_seq OWNED BY sesn.sesn__id;


--
-- TOC entry 205 (class 1259 OID 104094)
-- Name: sstm; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sstm (
    sstm__id bigint NOT NULL,
    sstmdscr character varying(500),
    sstnmbr character varying(50) NOT NULL
);


ALTER TABLE public.sstm OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 104100)
-- Name: sstm_sstm__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sstm_sstm__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sstm_sstm__id_seq OWNER TO postgres;

--
-- TOC entry 2306 (class 0 OID 0)
-- Dependencies: 206
-- Name: sstm_sstm__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE sstm_sstm__id_seq OWNED BY sstm.sstm__id;


--
-- TOC entry 207 (class 1259 OID 104102)
-- Name: tpac; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tpac (
    tpac__id integer NOT NULL,
    tpacdscr character varying(31) NOT NULL
);


ALTER TABLE public.tpac OWNER TO postgres;

--
-- TOC entry 2307 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE tpac; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tpac IS 'Tipo de accion';


--
-- TOC entry 208 (class 1259 OID 104105)
-- Name: tpac_tpac__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tpac_tpac__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tpac_tpac__id_seq OWNER TO postgres;

--
-- TOC entry 2308 (class 0 OID 0)
-- Dependencies: 208
-- Name: tpac_tpac__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tpac_tpac__id_seq OWNED BY tpac.tpac__id;


--
-- TOC entry 209 (class 1259 OID 104107)
-- Name: tpdc; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tpdc (
    tpdc__id integer NOT NULL,
    tpdccdgo character varying(4) NOT NULL,
    tpdcdscr character varying(31) NOT NULL
);


ALTER TABLE public.tpdc OWNER TO postgres;

--
-- TOC entry 2309 (class 0 OID 0)
-- Dependencies: 209
-- Name: TABLE tpdc; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tpdc IS 'Tipo de documento';


--
-- TOC entry 210 (class 1259 OID 104110)
-- Name: tpdc_tpdc__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tpdc_tpdc__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tpdc_tpdc__id_seq OWNER TO postgres;

--
-- TOC entry 2310 (class 0 OID 0)
-- Dependencies: 210
-- Name: tpdc_tpdc__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tpdc_tpdc__id_seq OWNED BY tpdc.tpdc__id;


--
-- TOC entry 211 (class 1259 OID 104112)
-- Name: tpdp; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tpdp (
    tpdp__id integer NOT NULL,
    tpdpcdgo character varying(4) NOT NULL,
    tpdpdscr character varying(31) NOT NULL
);


ALTER TABLE public.tpdp OWNER TO postgres;

--
-- TOC entry 2311 (class 0 OID 0)
-- Dependencies: 211
-- Name: TABLE tpdp; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tpdp IS 'Tipo de departamento';


--
-- TOC entry 212 (class 1259 OID 104115)
-- Name: tpdp_tpdp__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tpdp_tpdp__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tpdp_tpdp__id_seq OWNER TO postgres;

--
-- TOC entry 2312 (class 0 OID 0)
-- Dependencies: 212
-- Name: tpdp_tpdp__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tpdp_tpdp__id_seq OWNED BY tpdp.tpdp__id;


--
-- TOC entry 213 (class 1259 OID 104117)
-- Name: tppd; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tppd (
    tppd__id integer NOT NULL,
    tppdcdgo character varying(4) NOT NULL,
    tppddscr character varying(31) NOT NULL,
    tppdtmpo smallint
);


ALTER TABLE public.tppd OWNER TO postgres;

--
-- TOC entry 2313 (class 0 OID 0)
-- Dependencies: 213
-- Name: TABLE tppd; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tppd IS 'Tipo de prioridad del trámite';


--
-- TOC entry 214 (class 1259 OID 104120)
-- Name: tppd_tppd__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tppd_tppd__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tppd_tppd__id_seq OWNER TO postgres;

--
-- TOC entry 2314 (class 0 OID 0)
-- Dependencies: 214
-- Name: tppd_tppd__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tppd_tppd__id_seq OWNED BY tppd.tppd__id;


--
-- TOC entry 215 (class 1259 OID 104122)
-- Name: tppr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tppr (
    tppr__id integer NOT NULL,
    tpprcdgo character varying(1) NOT NULL,
    tpprdscr character varying(15) NOT NULL
);


ALTER TABLE public.tppr OWNER TO postgres;

--
-- TOC entry 2315 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE tppr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tppr IS 'Tipo de persona: natural o jurídica';


--
-- TOC entry 216 (class 1259 OID 104125)
-- Name: tppr_tppr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tppr_tppr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tppr_tppr__id_seq OWNER TO postgres;

--
-- TOC entry 2316 (class 0 OID 0)
-- Dependencies: 216
-- Name: tppr_tppr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tppr_tppr__id_seq OWNED BY tppr.tppr__id;


--
-- TOC entry 217 (class 1259 OID 104127)
-- Name: tptr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tptr (
    tptr__id integer NOT NULL,
    tptrcdgo character varying(4) NOT NULL,
    tptrdscr character varying(31) NOT NULL
);


ALTER TABLE public.tptr OWNER TO postgres;

--
-- TOC entry 2317 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE tptr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE tptr IS 'Tipo de trámite';


--
-- TOC entry 218 (class 1259 OID 104130)
-- Name: tptr_tptr__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tptr_tptr__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tptr_tptr__id_seq OWNER TO postgres;

--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 218
-- Name: tptr_tptr__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tptr_tptr__id_seq OWNED BY tptr.tptr__id;


--
-- TOC entry 219 (class 1259 OID 104132)
-- Name: trmt; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trmt (
    trmt__id integer NOT NULL,
    anio__id integer,
    trmtpdre integer,
    tpdc__id integer,
    prsn__de integer,
    tppr__id integer,
    edtr__id integer,
    tptr__id integer,
    orgn__id integer,
    trmtcdgo character varying(20),
    trmtasnt character varying(1023),
    trmtanxo character varying(1023),
    trmttxto text,
    trmtampz smallint,
    trmtextr character(1),
    trmtnota character varying(1023),
    trmtetdo character(1),
    tppd__id bigint,
    trmtobsr character varying(255),
    trmtnmro integer,
    trmtfccr timestamp without time zone,
    trmtfcen timestamp without time zone,
    trmtfcmd timestamp without time zone,
    trmtfcrv timestamp without time zone,
    dpto__de bigint
);


ALTER TABLE public.trmt OWNER TO postgres;

--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE trmt; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE trmt IS 'Tramites';


--
-- TOC entry 220 (class 1259 OID 104138)
-- Name: trmt_trmt__id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trmt_trmt__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trmt_trmt__id_seq OWNER TO postgres;

--
-- TOC entry 2320 (class 0 OID 0)
-- Dependencies: 220
-- Name: trmt_trmt__id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trmt_trmt__id_seq OWNED BY trmt.trmt__id;


--
-- TOC entry 1966 (class 2604 OID 104140)
-- Name: accn__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accn ALTER COLUMN accn__id SET DEFAULT nextval('accn_accn__id_seq'::regclass);


--
-- TOC entry 1967 (class 2604 OID 104141)
-- Name: accs__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accs ALTER COLUMN accs__id SET DEFAULT nextval('accs_accs__id_seq'::regclass);


--
-- TOC entry 1968 (class 2604 OID 104142)
-- Name: anio__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY anio ALTER COLUMN anio__id SET DEFAULT nextval('anio_anio__id_seq'::regclass);


--
-- TOC entry 1969 (class 2604 OID 104143)
-- Name: audt__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audt ALTER COLUMN audt__id SET DEFAULT nextval('audt_audt__id_seq'::regclass);


--
-- TOC entry 1970 (class 2604 OID 104144)
-- Name: ctrl__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ctrl ALTER COLUMN ctrl__id SET DEFAULT nextval('ctrl_ctrl__id_seq'::regclass);


--
-- TOC entry 1971 (class 2604 OID 104145)
-- Name: dctr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dctr ALTER COLUMN dctr__id SET DEFAULT nextval('dctr_dctr__id_seq'::regclass);


--
-- TOC entry 1972 (class 2604 OID 104146)
-- Name: ddlb__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ddlb ALTER COLUMN ddlb__id SET DEFAULT nextval('ddlb_ddlb__id_seq'::regclass);


--
-- TOC entry 1973 (class 2604 OID 104147)
-- Name: dpto__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dpto ALTER COLUMN dpto__id SET DEFAULT nextval('dpto_dpto__id_seq'::regclass);


--
-- TOC entry 1974 (class 2604 OID 104148)
-- Name: edtr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY edtr ALTER COLUMN edtr__id SET DEFAULT nextval('edtr_edtr__id_seq'::regclass);


--
-- TOC entry 1975 (class 2604 OID 104149)
-- Name: logf__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY logf ALTER COLUMN logf__id SET DEFAULT nextval('logf_logf__id_seq'::regclass);


--
-- TOC entry 1976 (class 2604 OID 104150)
-- Name: mdlo__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mdlo ALTER COLUMN mdlo__id SET DEFAULT nextval('mdlo_mdlo__id_seq'::regclass);


--
-- TOC entry 1977 (class 2604 OID 104151)
-- Name: nmro__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY nmro ALTER COLUMN nmro__id SET DEFAULT nextval('nmro_nmro__id_seq'::regclass);


--
-- TOC entry 1978 (class 2604 OID 104152)
-- Name: obtr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY obtr ALTER COLUMN obtr__id SET DEFAULT nextval('obtr_obtr__id_seq'::regclass);


--
-- TOC entry 1979 (class 2604 OID 104153)
-- Name: orgn__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orgn ALTER COLUMN orgn__id SET DEFAULT nextval('orgn_orgn__id_seq'::regclass);


--
-- TOC entry 1980 (class 2604 OID 104154)
-- Name: perm__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY perm ALTER COLUMN perm__id SET DEFAULT nextval('perm_perm__id_seq'::regclass);


--
-- TOC entry 1981 (class 2604 OID 104155)
-- Name: prfl__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prfl ALTER COLUMN prfl__id SET DEFAULT nextval('prfl_prfl__id_seq'::regclass);


--
-- TOC entry 1982 (class 2604 OID 104156)
-- Name: prms__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prms ALTER COLUMN prms__id SET DEFAULT nextval('prms_prms__id_seq'::regclass);


--
-- TOC entry 1983 (class 2604 OID 104157)
-- Name: prsn__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prsn ALTER COLUMN prsn__id SET DEFAULT nextval('prsn_prsn__id_seq'::regclass);


--
-- TOC entry 1984 (class 2604 OID 104158)
-- Name: prtr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prtr ALTER COLUMN prtr__id SET DEFAULT nextval('prtr_prtr__id_seq'::regclass);


--
-- TOC entry 1985 (class 2604 OID 104159)
-- Name: prus__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prus ALTER COLUMN prus__id SET DEFAULT nextval('prus_prus__id_seq'::regclass);


--
-- TOC entry 1986 (class 2604 OID 104160)
-- Name: rltr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rltr ALTER COLUMN rltr__id SET DEFAULT nextval('rltr_rltr__id_seq'::regclass);


--
-- TOC entry 1987 (class 2604 OID 104161)
-- Name: sesn__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sesn ALTER COLUMN sesn__id SET DEFAULT nextval('sesn_sesn__id_seq'::regclass);


--
-- TOC entry 1988 (class 2604 OID 104162)
-- Name: sstm__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sstm ALTER COLUMN sstm__id SET DEFAULT nextval('sstm_sstm__id_seq'::regclass);


--
-- TOC entry 1989 (class 2604 OID 104163)
-- Name: tpac__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tpac ALTER COLUMN tpac__id SET DEFAULT nextval('tpac_tpac__id_seq'::regclass);


--
-- TOC entry 1990 (class 2604 OID 104164)
-- Name: tpdc__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tpdc ALTER COLUMN tpdc__id SET DEFAULT nextval('tpdc_tpdc__id_seq'::regclass);


--
-- TOC entry 1991 (class 2604 OID 104165)
-- Name: tpdp__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tpdp ALTER COLUMN tpdp__id SET DEFAULT nextval('tpdp_tpdp__id_seq'::regclass);


--
-- TOC entry 1992 (class 2604 OID 104166)
-- Name: tppd__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tppd ALTER COLUMN tppd__id SET DEFAULT nextval('tppd_tppd__id_seq'::regclass);


--
-- TOC entry 1993 (class 2604 OID 104167)
-- Name: tppr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tppr ALTER COLUMN tppr__id SET DEFAULT nextval('tppr_tppr__id_seq'::regclass);


--
-- TOC entry 1994 (class 2604 OID 104168)
-- Name: tptr__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tptr ALTER COLUMN tptr__id SET DEFAULT nextval('tptr_tptr__id_seq'::regclass);


--
-- TOC entry 1995 (class 2604 OID 104169)
-- Name: trmt__id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt ALTER COLUMN trmt__id SET DEFAULT nextval('trmt_trmt__id_seq'::regclass);


--
-- TOC entry 2201 (class 0 OID 103963)
-- Dependencies: 161
-- Data for Name: accn; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY accn (accn__id, ctrl__id, mdlo__id, tpac__id, accnnmbr, accndscr, accnaudt) FROM stdin;
1	1	0	1	index	index	1
2	2	0	1	sacarAccn	sacarAccn	1
3	2	0	1	grabaAccn	grabaAccn	1
4	2	0	1	cargarControladores	cargarControladores	1
5	2	0	1	poneSQLnull	poneSQLnull	1
6	2	0	1	acciones	acciones	1
7	2	0	1	cargarAccionesPerfil	cargarAccionesPerfil	1
8	2	0	1	cargarAcciones	cargarAcciones	1
9	2	0	1	index	index	1
10	2	0	1	perfiles	perfiles	1
11	2	0	1	cambiarTipo	cambiarTipo	1
12	2	0	2	guardarPermisos	guardarPermisos	1
13	2	0	1	moverAccn	moverAccn	1
14	2	0	1	cambiarModuloControlador	cambiarModuloControlador	1
15	2	0	1	cambiarModulo	cambiarModulo	1
16	2	0	1	ajaxAcciones	ajaxAcciones	1
17	2	0	1	poneSQL	poneSQL	1
18	2	0	1	procesos	procesos	1
19	2	0	1	cambiaAccn	cambiaAccn	1
20	2	0	1	configurarAcciones	configurarAcciones	1
21	3	0	1	validarSesion	validarSesion	1
22	4	0	1	comprobarPassword	comprobarPassword	1
23	4	0	1	ataques	ataques	1
24	4	0	1	error	error	1
25	4	0	1	error404	error404	1
26	5	0	1	index	index	1
27	5	0	1	edit	edit	1
28	5	0	1	create	create	1
29	5	0	1	show	show	1
30	5	0	2	save	save	1
31	5	0	1	list	list	1
32	5	0	2	delete	delete	1
33	6	0	1	validarCedula_ajax	validarCedula_ajax	1
34	6	0	1	formUsuario_ajax	formUsuario_ajax	1
35	6	0	2	delete_ajax	delete_ajax	1
37	6	0	1	index	index	1
38	6	0	1	show_ajax	show_ajax	1
39	6	0	1	config	config	1
40	6	0	1	form_ajax	form_ajax	1
41	6	0	2	save_ajax	save_ajax	1
42	7	0	2	save	save	1
43	7	0	2	delete	delete	1
44	7	0	1	create	create	1
45	7	0	1	show	show	1
46	7	0	1	form	form	1
47	7	0	1	edit	edit	1
48	7	0	1	list	list	1
49	7	0	1	index	index	1
50	7	0	2	update	update	1
51	8	0	2	delete	delete	1
52	8	0	1	form	form	1
53	8	0	1	show	show	1
54	8	0	1	index	index	1
55	8	0	1	edit	edit	1
56	8	0	1	list	list	1
57	8	0	2	save	save	1
58	8	0	1	create	create	1
59	8	0	2	update	update	1
60	9	0	1	validar	validar	1
61	9	0	1	logout	logout	1
62	9	0	1	validarSesion	validarSesion	1
63	9	0	1	tests	tests	1
64	9	0	2	savePer	savePer	1
65	9	0	1	olvidoPass	olvidoPass	1
66	9	0	1	login	login	1
67	9	0	1	index	index	1
68	9	0	1	perfiles	perfiles	1
69	10	0	1	form_ajax	form_ajax	1
70	10	0	1	show_ajax	show_ajax	1
71	10	0	2	save_ajax	save_ajax	1
72	10	0	1	index	index	1
73	10	0	2	delete_ajax	delete_ajax	1
74	10	0	1	list	list	1
75	11	0	1	validarCodigo_ajax	validarCodigo_ajax	1
76	11	0	1	form_ajax	form_ajax	1
77	11	0	2	save_ajax	save_ajax	1
78	11	0	1	index	index	1
79	11	0	1	show_ajax	show_ajax	1
80	11	0	2	delete_ajax	delete_ajax	1
81	11	0	1	list	list	1
82	12	0	2	save_ajax	save_ajax	1
83	12	0	1	list	list	1
84	12	0	2	delete_ajax	delete_ajax	1
85	12	0	1	index	index	1
86	12	0	1	show_ajax	show_ajax	1
87	12	0	1	form_ajax	form_ajax	1
88	13	0	1	form_ajax	form_ajax	1
89	13	0	1	index	index	1
90	13	0	2	delete_ajax	delete_ajax	1
91	13	0	1	list	list	1
92	13	0	1	show_ajax	show_ajax	1
93	13	0	2	save_ajax	save_ajax	1
94	14	0	2	save_ajax	save_ajax	1
95	14	0	1	index	index	1
96	14	0	1	show_ajax	show_ajax	1
97	14	0	1	form_ajax	form_ajax	1
98	14	0	1	list	list	1
99	14	0	2	delete_ajax	delete_ajax	1
100	15	0	2	delete_ajax	delete_ajax	1
101	15	0	1	form_ajax	form_ajax	1
102	15	0	2	save_ajax	save_ajax	1
103	15	0	1	show_ajax	show_ajax	1
104	15	0	1	list	list	1
105	15	0	1	index	index	1
106	16	0	1	show_ajax	show_ajax	1
107	16	0	1	index	index	1
108	16	0	1	form_ajax	form_ajax	1
109	16	0	2	save_ajax	save_ajax	1
110	16	0	1	list	list	1
111	16	0	2	delete_ajax	delete_ajax	1
112	17	0	1	show_ajax	show_ajax	1
113	17	0	2	save_ajax	save_ajax	1
114	17	0	1	form_ajax	form_ajax	1
115	17	0	1	index	index	1
116	17	0	2	delete_ajax	delete_ajax	1
117	17	0	1	list	list	1
118	18	0	1	index	index	1
119	18	0	2	delete_ajax	delete_ajax	1
120	18	0	1	list	list	1
121	18	0	1	form_ajax	form_ajax	1
122	18	0	2	save_ajax	save_ajax	1
123	18	0	1	show_ajax	show_ajax	1
124	19	0	2	save_ajax	save_ajax	1
125	19	0	1	list	list	1
126	19	0	1	index	index	1
127	19	0	1	show_ajax	show_ajax	1
128	19	0	1	form_ajax	form_ajax	1
129	19	0	2	delete_ajax	delete_ajax	1
130	20	0	1	cargaUsuarios	cargaUsuarios	1
131	20	0	1	index	index	1
133	21	0	1	index	index	1
134	21	0	1	list	list	1
135	21	0	1	show_ajax	show_ajax	1
136	21	0	2	delete_ajax	delete_ajax	1
137	21	0	2	save_ajax	save_ajax	1
138	21	0	1	form_ajax	form_ajax	1
139	22	0	2	save_ajax	save_ajax	1
140	22	0	1	list	list	1
141	22	0	1	index	index	1
142	22	0	2	delete_ajax	delete_ajax	1
143	22	0	1	form_ajax	form_ajax	1
144	22	0	1	show_ajax	show_ajax	1
145	23	0	1	list	list	1
146	23	0	1	form_ajax	form_ajax	1
147	23	0	1	index	index	1
148	23	0	1	show_ajax	show_ajax	1
149	23	0	2	delete_ajax	delete_ajax	1
150	23	0	2	save_ajax	save_ajax	1
151	24	0	1	index	index	1
152	24	0	2	delete_ajax	delete_ajax	1
153	24	0	1	show_ajax	show_ajax	1
154	24	0	2	save_ajax	save_ajax	1
155	24	0	1	list	list	1
156	24	0	1	form_ajax	form_ajax	1
157	25	0	1	index	index	1
158	25	0	1	form_ajax	form_ajax	1
159	25	0	1	list	list	1
160	25	0	1	show_ajax	show_ajax	1
161	25	0	2	delete_ajax	delete_ajax	1
162	25	0	2	save_ajax	save_ajax	1
163	26	0	2	delete_ajax	delete_ajax	1
164	26	0	2	save_ajax	save_ajax	1
165	26	0	1	list	list	1
166	26	0	1	index	index	1
167	26	0	1	form_ajax	form_ajax	1
168	26	0	1	show_ajax	show_ajax	1
169	27	0	2	save_ajax	save_ajax	1
170	27	0	1	form_ajax	form_ajax	1
171	27	0	1	list	list	1
172	27	0	2	delete_ajax	delete_ajax	1
173	27	0	1	index	index	1
174	27	0	1	show_ajax	show_ajax	1
175	28	0	2	saveCalendario	saveCalendario	1
176	28	0	1	list	list	1
177	28	0	2	delete	delete	1
178	28	0	1	index	index	1
180	28	0	1	calculador	calculador	1
181	28	0	1	form_ajax	form_ajax	1
182	28	0	1	show_ajax	show_ajax	1
183	28	0	1	calcDias	calcDias	1
184	28	0	1	calcEntre	calcEntre	1
185	28	0	2	save	save	1
186	29	0	1	index	index	1
187	29	0	1	secuencias	secuencias	1
188	29	0	1	valoresDominios	valoresDominios	1
189	30	0	1	excel	excel	1
190	30	0	1	index	index	1
191	33	0	1	show	show	1
192	33	0	1	form_ajax	form_ajax	1
193	33	0	1	create	create	1
194	33	0	2	delete_ajax	delete_ajax	1
195	33	0	1	index	index	1
196	33	0	2	update	update	1
197	33	0	1	show_ajax	show_ajax	1
198	33	0	2	save_ajax	save_ajax	1
199	33	0	1	form	form	1
200	33	0	1	edit	edit	1
201	33	0	2	save	save	1
202	33	0	2	delete	delete	1
203	33	0	1	list	list	1
204	32	0	1	form_ajax	form_ajax	1
205	32	0	1	grabaMdlo	grabaMdlo	1
206	32	0	2	delete	delete	1
207	32	0	1	creaMdlo	creaMdlo	1
208	32	0	1	creaPrfl	creaPrfl	1
209	32	0	2	update	update	1
210	32	0	1	edit	edit	1
211	32	0	1	grabaPrfl	grabaPrfl	1
212	32	0	1	show_ajax	show_ajax	1
213	32	0	1	editMdlo	editMdlo	1
214	32	0	1	form	form	1
215	32	0	2	delete_ajax	delete_ajax	1
216	32	0	1	editPrfl	editPrfl	1
217	32	0	1	lstaModulos	lstaModulos	1
218	32	0	1	verMenu	verMenu	1
219	32	0	1	modulos	modulos	1
220	32	0	2	save_ajax	save_ajax	1
221	32	0	1	grabar	grabar	1
222	32	0	1	borraPrfl	borraPrfl	1
223	32	0	1	create	create	1
224	32	0	1	index	index	1
225	32	0	1	borraMdlo	borraMdlo	1
226	32	0	1	show	show	1
227	32	0	2	save	save	1
228	32	0	1	list	list	1
229	32	0	1	ajaxPermisos	ajaxPermisos	1
230	31	0	1	cambiarModuloControlador	cambiarModuloControlador	1
231	31	0	1	configurarAcciones	configurarAcciones	1
232	31	0	1	cargarAccionesPerfil	cargarAccionesPerfil	1
233	31	0	1	cargarAcciones	cargarAcciones	1
234	31	0	1	cambiarModulo	cambiarModulo	1
235	31	0	1	ajaxAcciones	ajaxAcciones	1
236	31	0	1	moverAccn	moverAccn	1
237	31	0	1	procesos	procesos	1
238	31	0	2	guardarPermisos	guardarPermisos	1
239	31	0	1	poneSQLnull	poneSQLnull	1
240	31	0	1	grabaAccn	grabaAccn	1
241	31	0	1	cargarControladores	cargarControladores	1
243	31	0	1	sacarAccn	sacarAccn	1
244	31	0	1	poneSQL	poneSQL	1
245	31	0	1	perfiles	perfiles	1
248	31	0	1	cambiaAccn	cambiaAccn	1
246	31	3	1	acciones	acciones	1
242	31	0	1	cambiarTipo	cambiarTipo	1
132	20	2	1	crearTramite	Crear Trámite	1
332	35	0	1	sampleInclude	sampleInclude	1
249	6	0	1	terminarPermiso_ajax	terminarPermiso_ajax	1
250	6	0	1	eliminarPermiso_ajax	eliminarPermiso_ajax	1
251	6	0	2	savePerfiles_ajax	savePerfiles_ajax	1
252	6	0	2	saveAccesos_ajax	saveAccesos_ajax	1
253	6	0	2	savePermisos_ajax	savePermisos_ajax	1
254	6	0	1	eliminarAcceso_ajax	eliminarAcceso_ajax	1
255	6	0	1	accesos	accesos	1
256	6	0	1	terminarAcceso_ajax	terminarAcceso_ajax	1
257	6	0	1	permisos	permisos	1
258	9	0	1	cambiarPass	cambiarPass	1
259	9	0	1	validarPass	validarPass	1
260	9	0	2	guardarPass	guardarPass	1
261	20	0	2	save	save	1
262	20	0	1	randomDep	randomDep	1
263	20	0	1	redactarTramite	redactarTramite	1
333	35	0	1	pdfLink	pdfLink	1
179	28	3	1	calendario	Días laborables	1
265	25	0	1	validarCodigo_ajax	validarCodigo_ajax	1
266	20	0	1	busquedaArchivados	busquedaArchivados	1
267	20	0	1	tablaBandeja	tablaBandeja	1
268	20	0	1	tablaArchivados	tablaArchivados	1
269	20	0	1	busquedaBandeja	busquedaBandeja	1
272	14	0	1	validarCodigo_ajax	validarCodigo_ajax	1
273	12	0	1	validarAnio_ajax	validarAnio_ajax	1
36	6	3	1	list	Usuarios	1
334	35	0	1	demo	demo	1
271	20	2	1	bandejaEntrada	Bandeja de Entrada	1
335	35	0	1	demo2	demo2	1
336	35	0	1	index	index	1
337	36	0	1	crearPdf	crearPdf	1
247	31	0	1	index	Acciones	1
274	20	0	1	redactar	redactar	1
264	34	1	1	index	Inicio	1
338	36	0	1	verPdf	verPdf	1
275	34	3	1	parametros	Parámetros	1
276	15	0	1	getLista	getLista	1
277	19	0	1	getLista	getLista	1
278	11	0	1	getLista	getLista	1
279	17	0	1	getLista	getLista	1
280	25	0	1	getLista	getLista	1
282	20	0	1	observaciones	observaciones	1
283	20	0	1	rojoPendiente	rojoPendiente	1
284	20	0	1	alertRecibidos	alertRecibidos	1
285	20	0	1	alertaPendientes	alertaPendientes	1
286	20	0	2	guardarObservacion	guardarObservacion	1
288	20	0	1	alertaRetrasados	alertaRetrasados	1
289	24	0	1	getLista	getLista	1
290	14	0	1	getLista	getLista	1
291	22	0	1	getLista	getLista	1
292	21	0	1	getLista	getLista	1
293	10	0	1	getLista	getLista	1
294	26	0	1	getLista	getLista	1
295	13	0	1	getLista	getLista	1
296	16	0	1	getLista	getLista	1
297	12	0	1	getLista	getLista	1
298	27	0	1	getLista	getLista	1
299	23	0	1	getLista	getLista	1
300	6	0	1	validarPass_ajax	validarPass_ajax	1
301	6	0	2	savePass_ajax	savePass_ajax	1
302	6	0	1	uploadFile	uploadFile	1
303	6	0	1	getLista	getLista	1
304	6	0	1	loadFoto	loadFoto	1
305	6	0	1	personal	personal	1
306	6	0	1	resizeCropImage	resizeCropImage	1
339	4	0	1	internalServerError	internalServerError	1
340	4	0	1	notFound	notFound	1
287	39	0	1	tablaBandejaSalida	tablaBandejaSalida	1
281	39	2	1	bandejaSalida	Bandeja de Salida	1
307	38	0	1	uploader	uploader	1
308	38	0	1	browser	browser	1
309	38	0	1	index	index	1
310	38	0	2	delete_ajax	delete_ajax	1
312	40	0	1	tablaBusquedaTramite	tablaBusquedaTramite	1
313	39	0	2	saveNotas	saveNotas	1
314	39	0	1	revisar	revisar	1
315	39	0	1	verTramite	verTramite	1
316	39	0	1	revision	revision	1
317	39	0	1	busquedaBandejaSalida	busquedaBandejaSalida	1
318	39	0	1	enviar	enviar	1
319	39	0	1	verRezagados	verRezagados	1
321	37	0	1	errores	errores	1
322	37	0	1	creaHtmlSeguimiento	creaHtmlSeguimiento	1
323	37	0	1	creaHtmlVer	creaHtmlVer	1
324	37	0	1	verTramite	verTramite	1
325	37	0	1	seguimientoTramite	seguimientoTramite	1
326	37	0	2	save	save	1
327	37	0	1	enviarTramiteJefe	enviarTramiteJefe	1
328	37	0	1	tablaBandejaEntradaDpto	tablaBandejaEntradaDpto	1
329	37	0	1	recibirTramite	recibirTramite	1
330	35	0	1	demo3	demo3	1
331	35	0	1	pdfForm	pdfForm	1
341	4	0	1	forbidden	forbidden	1
342	4	0	1	unauthorized	unauthorized	1
343	6	0	1	validarMail_ajax	validarMail_ajax	1
344	11	0	1	arbol	arbol	1
345	11	0	1	loadTreePart	loadTreePart	1
346	11	0	1	makeTreeNode	makeTreeNode	1
348	20	0	1	anular	anular	1
349	20	0	1	todaDescendencia	todaDescendencia	1
350	20	0	1	recibir	recibir	1
351	20	0	2	saveTramite	saveTramite	1
352	20	0	1	busquedaBandejaSalida	busquedaBandejaSalida	1
353	20	0	1	tablaBandejaSalidaDepartamento	tablaBandejaSalidaDepartamento	1
354	20	0	2	guardarRecibir	guardarRecibir	1
355	20	0	1	observacionArchivado	observacionArchivado	1
356	20	0	1	revisarHijos	revisarHijos	1
357	20	0	2	save_bck	save_bck	1
270	20	2	1	archivados	Trámites Archivados	1
347	20	2	1	anulados	Trámites Anulados	1
358	20	0	1	archivar	archivar	1
359	20	0	1	tablaAnulados	tablaAnulados	1
360	20	0	1	bandejaSalidaDepartamento	bandejaSalidaDepartamento	1
320	37	2	1	bandejaEntradaDpto	Bandeja de Entrada Oficina	1
311	40	2	1	busquedaTramite	Buscar Trámites	1
\.


--
-- TOC entry 2321 (class 0 OID 0)
-- Dependencies: 162
-- Name: accn_accn__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('accn_accn__id_seq', 360, true);


--
-- TOC entry 2203 (class 0 OID 103968)
-- Dependencies: 163
-- Data for Name: accs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY accs (accs__id, prsn__id, accsfcin, accsfcfn, accsobsr, usro__id, prsnasgn) FROM stdin;
\.


--
-- TOC entry 2322 (class 0 OID 0)
-- Dependencies: 164
-- Name: accs_accs__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('accs_accs__id_seq', 12, true);


--
-- TOC entry 2205 (class 0 OID 103973)
-- Dependencies: 165
-- Data for Name: anio; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY anio (anio__id, anionmro) FROM stdin;
1	2014
2	2015
\.


--
-- TOC entry 2323 (class 0 OID 0)
-- Dependencies: 166
-- Name: anio_anio__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('anio_anio__id_seq', 8, true);


--
-- TOC entry 2207 (class 0 OID 103978)
-- Dependencies: 167
-- Data for Name: audt; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY audt (audt__id, audtaccn, audtcmpo, audtctrl, audttbla, audtfcha, audtnewv, audtoldv, audtoprc, prfl__id, reg_id, usro__id) FROM stdin;
\.


--
-- TOC entry 2324 (class 0 OID 0)
-- Dependencies: 168
-- Name: audt_audt__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('audt_audt__id_seq', 1, false);


--
-- TOC entry 2209 (class 0 OID 103986)
-- Dependencies: 169
-- Data for Name: ctrl; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ctrl (ctrl__id, ctrlnmbr) FROM stdin;
1	Dbdoc
2	AccionesOld
3	ValidarSesion
4	Shield
5	Sesn
6	Persona
7	Sistema
8	Ctrl
9	Login
10	Numero
11	Departamento
12	Anio
13	ObservacionTramite
14	TipoDocumento
15	PermisoTramite
16	PermisoUsuario
17	TipoTramite
18	PermisoDocumentoTramite
19	OrigenTramite
20	Tramite
21	EstadoTramite
22	RolPersonaTramite
23	TipoPrioridad
24	PersonaDocumentoTramite
25	TipoPersona
26	DocumentoTramite
27	TipoDepartamento
28	DiaLaborable
29	DomainFix
30	Excel
31	Acciones
32	Prfl
33	Modulo
34	Inicio
35	Pdf
36	TramiteExport
37	Tramite3
38	TramiteImagenes
39	Tramite2
40	BuscarTramite
\.


--
-- TOC entry 2325 (class 0 OID 0)
-- Dependencies: 170
-- Name: ctrl_ctrl__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ctrl_ctrl__id_seq', 40, true);


--
-- TOC entry 2211 (class 0 OID 103991)
-- Dependencies: 171
-- Data for Name: dctr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dctr (dctr__id, trmt__id, trmtanxo, dctrfcha, dctrrsmn, dctrclve, dcmtpath, dctrdscr, dctrfcrv) FROM stdin;
\.


--
-- TOC entry 2326 (class 0 OID 0)
-- Dependencies: 172
-- Name: dctr_dctr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('dctr_dctr__id_seq', 1, false);


--
-- TOC entry 2213 (class 0 OID 103999)
-- Dependencies: 173
-- Data for Name: ddlb; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ddlb (ddlb__id, ddlbanio, ddlbddia, ddlbfcha, ddlbobsr, ddlbordn) FROM stdin;
4	2014	6	2014-01-04 00:00:00	\N	0
5	2014	0	2014-01-05 00:00:00	\N	0
11	2014	6	2014-01-11 00:00:00	\N	0
12	2014	0	2014-01-12 00:00:00	\N	0
18	2014	6	2014-01-18 00:00:00	\N	0
19	2014	0	2014-01-19 00:00:00	\N	0
25	2014	6	2014-01-25 00:00:00	\N	0
26	2014	0	2014-01-26 00:00:00	\N	0
32	2014	6	2014-02-01 00:00:00	\N	0
33	2014	0	2014-02-02 00:00:00	\N	0
39	2014	6	2014-02-08 00:00:00	\N	0
40	2014	0	2014-02-09 00:00:00	\N	0
46	2014	6	2014-02-15 00:00:00	\N	0
47	2014	0	2014-02-16 00:00:00	\N	0
53	2014	6	2014-02-22 00:00:00	\N	0
54	2014	0	2014-02-23 00:00:00	\N	0
60	2014	6	2014-03-01 00:00:00	\N	0
61	2014	0	2014-03-02 00:00:00	\N	0
67	2014	6	2014-03-08 00:00:00	\N	0
68	2014	0	2014-03-09 00:00:00	\N	0
74	2014	6	2014-03-15 00:00:00	\N	0
75	2014	0	2014-03-16 00:00:00	\N	0
81	2014	6	2014-03-22 00:00:00	\N	0
82	2014	0	2014-03-23 00:00:00	\N	0
88	2014	6	2014-03-29 00:00:00	\N	0
89	2014	0	2014-03-30 00:00:00	\N	0
95	2014	6	2014-04-05 00:00:00	\N	0
96	2014	0	2014-04-06 00:00:00	\N	0
102	2014	6	2014-04-12 00:00:00	\N	0
103	2014	0	2014-04-13 00:00:00	\N	0
109	2014	6	2014-04-19 00:00:00	\N	0
110	2014	0	2014-04-20 00:00:00	\N	0
116	2014	6	2014-04-26 00:00:00	\N	0
117	2014	0	2014-04-27 00:00:00	\N	0
123	2014	6	2014-05-03 00:00:00	\N	0
124	2014	0	2014-05-04 00:00:00	\N	0
130	2014	6	2014-05-10 00:00:00	\N	0
131	2014	0	2014-05-11 00:00:00	\N	0
137	2014	6	2014-05-17 00:00:00	\N	0
138	2014	0	2014-05-18 00:00:00	\N	0
144	2014	6	2014-05-24 00:00:00	\N	0
145	2014	0	2014-05-25 00:00:00	\N	0
2	2014	4	2014-01-02 00:00:00	\N	1
3	2014	5	2014-01-03 00:00:00	\N	2
6	2014	1	2014-01-06 00:00:00	\N	3
7	2014	2	2014-01-07 00:00:00	\N	4
8	2014	3	2014-01-08 00:00:00	\N	5
9	2014	4	2014-01-09 00:00:00	\N	6
10	2014	5	2014-01-10 00:00:00	\N	7
13	2014	1	2014-01-13 00:00:00	\N	8
14	2014	2	2014-01-14 00:00:00	\N	9
15	2014	3	2014-01-15 00:00:00	\N	10
16	2014	4	2014-01-16 00:00:00	\N	11
17	2014	5	2014-01-17 00:00:00	\N	12
21	2014	2	2014-01-21 00:00:00	\N	14
22	2014	3	2014-01-22 00:00:00	\N	15
23	2014	4	2014-01-23 00:00:00	\N	16
24	2014	5	2014-01-24 00:00:00	\N	17
27	2014	1	2014-01-27 00:00:00	\N	18
28	2014	2	2014-01-28 00:00:00	\N	19
29	2014	3	2014-01-29 00:00:00	\N	20
30	2014	4	2014-01-30 00:00:00	\N	21
31	2014	5	2014-01-31 00:00:00	\N	22
34	2014	1	2014-02-03 00:00:00	\N	23
35	2014	2	2014-02-04 00:00:00	\N	24
36	2014	3	2014-02-05 00:00:00	\N	25
37	2014	4	2014-02-06 00:00:00	\N	26
41	2014	1	2014-02-10 00:00:00	\N	28
42	2014	2	2014-02-11 00:00:00	\N	29
43	2014	3	2014-02-12 00:00:00	\N	30
44	2014	4	2014-02-13 00:00:00	\N	31
45	2014	5	2014-02-14 00:00:00	\N	32
48	2014	1	2014-02-17 00:00:00	\N	33
49	2014	2	2014-02-18 00:00:00	\N	34
50	2014	3	2014-02-19 00:00:00	\N	35
51	2014	4	2014-02-20 00:00:00	\N	36
52	2014	5	2014-02-21 00:00:00	\N	37
55	2014	1	2014-02-24 00:00:00	\N	38
56	2014	2	2014-02-25 00:00:00	\N	39
57	2014	3	2014-02-26 00:00:00	\N	40
59	2014	5	2014-02-28 00:00:00	\N	42
63	2014	2	2014-03-04 00:00:00	\N	0
64	2014	3	2014-03-05 00:00:00	\N	43
65	2014	4	2014-03-06 00:00:00	\N	44
66	2014	5	2014-03-07 00:00:00	\N	45
69	2014	1	2014-03-10 00:00:00	\N	46
70	2014	2	2014-03-11 00:00:00	\N	47
71	2014	3	2014-03-12 00:00:00	\N	48
72	2014	4	2014-03-13 00:00:00	\N	49
73	2014	5	2014-03-14 00:00:00	\N	50
76	2014	1	2014-03-17 00:00:00	\N	51
77	2014	2	2014-03-18 00:00:00	\N	52
79	2014	4	2014-03-20 00:00:00	\N	54
80	2014	5	2014-03-21 00:00:00	\N	55
83	2014	1	2014-03-24 00:00:00	\N	56
84	2014	2	2014-03-25 00:00:00	\N	57
85	2014	3	2014-03-26 00:00:00	\N	58
86	2014	4	2014-03-27 00:00:00	\N	59
87	2014	5	2014-03-28 00:00:00	\N	60
90	2014	1	2014-03-31 00:00:00	\N	61
91	2014	2	2014-04-01 00:00:00	\N	62
92	2014	3	2014-04-02 00:00:00	\N	63
93	2014	4	2014-04-03 00:00:00	\N	64
94	2014	5	2014-04-04 00:00:00	\N	65
97	2014	1	2014-04-07 00:00:00	\N	66
99	2014	3	2014-04-09 00:00:00	\N	68
100	2014	4	2014-04-10 00:00:00	\N	69
101	2014	5	2014-04-11 00:00:00	\N	70
104	2014	1	2014-04-14 00:00:00	\N	71
105	2014	2	2014-04-15 00:00:00	\N	72
106	2014	3	2014-04-16 00:00:00	\N	73
107	2014	4	2014-04-17 00:00:00	\N	74
108	2014	5	2014-04-18 00:00:00	\N	75
111	2014	1	2014-04-21 00:00:00	\N	76
112	2014	2	2014-04-22 00:00:00	\N	77
113	2014	3	2014-04-23 00:00:00	\N	78
114	2014	4	2014-04-24 00:00:00	\N	79
115	2014	5	2014-04-25 00:00:00	\N	80
119	2014	2	2014-04-29 00:00:00	\N	82
120	2014	3	2014-04-30 00:00:00	\N	83
121	2014	4	2014-05-01 00:00:00	\N	84
122	2014	5	2014-05-02 00:00:00	\N	85
125	2014	1	2014-05-05 00:00:00	\N	86
126	2014	2	2014-05-06 00:00:00	\N	87
127	2014	3	2014-05-07 00:00:00	\N	88
128	2014	4	2014-05-08 00:00:00	\N	89
129	2014	5	2014-05-09 00:00:00	\N	90
132	2014	1	2014-05-12 00:00:00	\N	91
133	2014	2	2014-05-13 00:00:00	\N	92
134	2014	3	2014-05-14 00:00:00	\N	93
135	2014	4	2014-05-15 00:00:00	\N	94
139	2014	1	2014-05-19 00:00:00	\N	96
140	2014	2	2014-05-20 00:00:00	\N	97
141	2014	3	2014-05-21 00:00:00	\N	98
142	2014	4	2014-05-22 00:00:00	\N	99
143	2014	5	2014-05-23 00:00:00	\N	100
62	2014	1	2014-03-03 00:00:00	\N	0
151	2014	6	2014-05-31 00:00:00	\N	0
152	2014	0	2014-06-01 00:00:00	\N	0
158	2014	6	2014-06-07 00:00:00	\N	0
159	2014	0	2014-06-08 00:00:00	\N	0
165	2014	6	2014-06-14 00:00:00	\N	0
166	2014	0	2014-06-15 00:00:00	\N	0
172	2014	6	2014-06-21 00:00:00	\N	0
173	2014	0	2014-06-22 00:00:00	\N	0
179	2014	6	2014-06-28 00:00:00	\N	0
180	2014	0	2014-06-29 00:00:00	\N	0
186	2014	6	2014-07-05 00:00:00	\N	0
187	2014	0	2014-07-06 00:00:00	\N	0
193	2014	6	2014-07-12 00:00:00	\N	0
194	2014	0	2014-07-13 00:00:00	\N	0
200	2014	6	2014-07-19 00:00:00	\N	0
201	2014	0	2014-07-20 00:00:00	\N	0
207	2014	6	2014-07-26 00:00:00	\N	0
208	2014	0	2014-07-27 00:00:00	\N	0
214	2014	6	2014-08-02 00:00:00	\N	0
215	2014	0	2014-08-03 00:00:00	\N	0
221	2014	6	2014-08-09 00:00:00	\N	0
222	2014	0	2014-08-10 00:00:00	\N	0
228	2014	6	2014-08-16 00:00:00	\N	0
229	2014	0	2014-08-17 00:00:00	\N	0
235	2014	6	2014-08-23 00:00:00	\N	0
236	2014	0	2014-08-24 00:00:00	\N	0
242	2014	6	2014-08-30 00:00:00	\N	0
243	2014	0	2014-08-31 00:00:00	\N	0
249	2014	6	2014-09-06 00:00:00	\N	0
250	2014	0	2014-09-07 00:00:00	\N	0
256	2014	6	2014-09-13 00:00:00	\N	0
257	2014	0	2014-09-14 00:00:00	\N	0
263	2014	6	2014-09-20 00:00:00	\N	0
264	2014	0	2014-09-21 00:00:00	\N	0
270	2014	6	2014-09-27 00:00:00	\N	0
271	2014	0	2014-09-28 00:00:00	\N	0
277	2014	6	2014-10-04 00:00:00	\N	0
278	2014	0	2014-10-05 00:00:00	\N	0
284	2014	6	2014-10-11 00:00:00	\N	0
285	2014	0	2014-10-12 00:00:00	\N	0
148	2014	3	2014-05-28 00:00:00	\N	103
149	2014	4	2014-05-29 00:00:00	\N	104
150	2014	5	2014-05-30 00:00:00	\N	105
153	2014	1	2014-06-02 00:00:00	\N	106
154	2014	2	2014-06-03 00:00:00	\N	107
155	2014	3	2014-06-04 00:00:00	\N	108
156	2014	4	2014-06-05 00:00:00	\N	109
157	2014	5	2014-06-06 00:00:00	\N	110
160	2014	1	2014-06-09 00:00:00	\N	111
161	2014	2	2014-06-10 00:00:00	\N	112
162	2014	3	2014-06-11 00:00:00	\N	113
164	2014	5	2014-06-13 00:00:00	\N	115
167	2014	1	2014-06-16 00:00:00	\N	116
168	2014	2	2014-06-17 00:00:00	\N	117
169	2014	3	2014-06-18 00:00:00	\N	118
170	2014	4	2014-06-19 00:00:00	\N	119
171	2014	5	2014-06-20 00:00:00	\N	120
174	2014	1	2014-06-23 00:00:00	\N	121
175	2014	2	2014-06-24 00:00:00	\N	122
176	2014	3	2014-06-25 00:00:00	\N	123
177	2014	4	2014-06-26 00:00:00	\N	124
178	2014	5	2014-06-27 00:00:00	\N	125
181	2014	1	2014-06-30 00:00:00	\N	126
182	2014	2	2014-07-01 00:00:00	\N	127
184	2014	4	2014-07-03 00:00:00	\N	129
185	2014	5	2014-07-04 00:00:00	\N	130
188	2014	1	2014-07-07 00:00:00	\N	131
189	2014	2	2014-07-08 00:00:00	\N	132
190	2014	3	2014-07-09 00:00:00	\N	133
191	2014	4	2014-07-10 00:00:00	\N	134
192	2014	5	2014-07-11 00:00:00	\N	135
195	2014	1	2014-07-14 00:00:00	\N	136
196	2014	2	2014-07-15 00:00:00	\N	137
197	2014	3	2014-07-16 00:00:00	\N	138
198	2014	4	2014-07-17 00:00:00	\N	139
199	2014	5	2014-07-18 00:00:00	\N	140
202	2014	1	2014-07-21 00:00:00	\N	141
204	2014	3	2014-07-23 00:00:00	\N	143
205	2014	4	2014-07-24 00:00:00	\N	144
206	2014	5	2014-07-25 00:00:00	\N	145
209	2014	1	2014-07-28 00:00:00	\N	146
210	2014	2	2014-07-29 00:00:00	\N	147
211	2014	3	2014-07-30 00:00:00	\N	148
212	2014	4	2014-07-31 00:00:00	\N	149
213	2014	5	2014-08-01 00:00:00	\N	150
216	2014	1	2014-08-04 00:00:00	\N	151
217	2014	2	2014-08-05 00:00:00	\N	152
218	2014	3	2014-08-06 00:00:00	\N	153
219	2014	4	2014-08-07 00:00:00	\N	154
220	2014	5	2014-08-08 00:00:00	\N	155
224	2014	2	2014-08-12 00:00:00	\N	157
225	2014	3	2014-08-13 00:00:00	\N	158
226	2014	4	2014-08-14 00:00:00	\N	159
227	2014	5	2014-08-15 00:00:00	\N	160
230	2014	1	2014-08-18 00:00:00	\N	161
231	2014	2	2014-08-19 00:00:00	\N	162
232	2014	3	2014-08-20 00:00:00	\N	163
233	2014	4	2014-08-21 00:00:00	\N	164
234	2014	5	2014-08-22 00:00:00	\N	165
237	2014	1	2014-08-25 00:00:00	\N	166
238	2014	2	2014-08-26 00:00:00	\N	167
239	2014	3	2014-08-27 00:00:00	\N	168
240	2014	4	2014-08-28 00:00:00	\N	169
244	2014	1	2014-09-01 00:00:00	\N	171
245	2014	2	2014-09-02 00:00:00	\N	172
246	2014	3	2014-09-03 00:00:00	\N	173
247	2014	4	2014-09-04 00:00:00	\N	174
248	2014	5	2014-09-05 00:00:00	\N	175
251	2014	1	2014-09-08 00:00:00	\N	176
252	2014	2	2014-09-09 00:00:00	\N	177
253	2014	3	2014-09-10 00:00:00	\N	178
254	2014	4	2014-09-11 00:00:00	\N	179
255	2014	5	2014-09-12 00:00:00	\N	180
258	2014	1	2014-09-15 00:00:00	\N	181
259	2014	2	2014-09-16 00:00:00	\N	182
260	2014	3	2014-09-17 00:00:00	\N	183
262	2014	5	2014-09-19 00:00:00	\N	185
265	2014	1	2014-09-22 00:00:00	\N	186
266	2014	2	2014-09-23 00:00:00	\N	187
267	2014	3	2014-09-24 00:00:00	\N	188
268	2014	4	2014-09-25 00:00:00	\N	189
269	2014	5	2014-09-26 00:00:00	\N	190
272	2014	1	2014-09-29 00:00:00	\N	191
273	2014	2	2014-09-30 00:00:00	\N	192
274	2014	3	2014-10-01 00:00:00	\N	193
275	2014	4	2014-10-02 00:00:00	\N	194
276	2014	5	2014-10-03 00:00:00	\N	195
279	2014	1	2014-10-06 00:00:00	\N	196
280	2014	2	2014-10-07 00:00:00	\N	197
282	2014	4	2014-10-09 00:00:00	\N	199
283	2014	5	2014-10-10 00:00:00	\N	200
286	2014	1	2014-10-13 00:00:00	\N	201
287	2014	2	2014-10-14 00:00:00	\N	202
288	2014	3	2014-10-15 00:00:00	\N	203
289	2014	4	2014-10-16 00:00:00	\N	204
290	2014	5	2014-10-17 00:00:00	\N	205
147	2014	2	2014-05-27 00:00:00	\N	102
291	2014	6	2014-10-18 00:00:00	\N	0
292	2014	0	2014-10-19 00:00:00	\N	0
298	2014	6	2014-10-25 00:00:00	\N	0
299	2014	0	2014-10-26 00:00:00	\N	0
305	2014	6	2014-11-01 00:00:00	\N	0
306	2014	0	2014-11-02 00:00:00	\N	0
312	2014	6	2014-11-08 00:00:00	\N	0
313	2014	0	2014-11-09 00:00:00	\N	0
319	2014	6	2014-11-15 00:00:00	\N	0
320	2014	0	2014-11-16 00:00:00	\N	0
326	2014	6	2014-11-22 00:00:00	\N	0
327	2014	0	2014-11-23 00:00:00	\N	0
333	2014	6	2014-11-29 00:00:00	\N	0
334	2014	0	2014-11-30 00:00:00	\N	0
340	2014	6	2014-12-06 00:00:00	\N	0
341	2014	0	2014-12-07 00:00:00	\N	0
347	2014	6	2014-12-13 00:00:00	\N	0
348	2014	0	2014-12-14 00:00:00	\N	0
354	2014	6	2014-12-20 00:00:00	\N	0
355	2014	0	2014-12-21 00:00:00	\N	0
361	2014	6	2014-12-27 00:00:00	\N	0
362	2014	0	2014-12-28 00:00:00	\N	0
1	2014	3	2014-01-01 00:00:00	\N	0
20	2014	1	2014-01-20 00:00:00	\N	13
38	2014	5	2014-02-07 00:00:00	\N	27
58	2014	4	2014-02-27 00:00:00	\N	41
304	2014	5	2014-10-31 00:00:00	\N	215
307	2014	1	2014-11-03 00:00:00	\N	216
308	2014	2	2014-11-04 00:00:00	\N	217
309	2014	3	2014-11-05 00:00:00	\N	218
310	2014	4	2014-11-06 00:00:00	\N	219
311	2014	5	2014-11-07 00:00:00	\N	220
314	2014	1	2014-11-10 00:00:00	\N	221
315	2014	2	2014-11-11 00:00:00	\N	222
316	2014	3	2014-11-12 00:00:00	\N	223
356	2014	1	2014-12-22 00:00:00	\N	251
357	2014	2	2014-12-23 00:00:00	\N	252
358	2014	3	2014-12-24 00:00:00	\N	253
360	2014	5	2014-12-26 00:00:00	\N	254
363	2014	1	2014-12-29 00:00:00	\N	255
364	2014	2	2014-12-30 00:00:00	\N	256
365	2014	3	2014-12-31 00:00:00	\N	257
366	2013	2	2013-01-01 00:00:00	\N	1
367	2013	3	2013-01-02 00:00:00	\N	2
368	2013	4	2013-01-03 00:00:00	\N	3
369	2013	5	2013-01-04 00:00:00	\N	4
370	2013	6	2013-01-05 00:00:00	\N	0
371	2013	0	2013-01-06 00:00:00	\N	0
372	2013	1	2013-01-07 00:00:00	\N	5
373	2013	2	2013-01-08 00:00:00	\N	6
374	2013	3	2013-01-09 00:00:00	\N	7
375	2013	4	2013-01-10 00:00:00	\N	8
376	2013	5	2013-01-11 00:00:00	\N	9
377	2013	6	2013-01-12 00:00:00	\N	0
378	2013	0	2013-01-13 00:00:00	\N	0
379	2013	1	2013-01-14 00:00:00	\N	10
380	2013	2	2013-01-15 00:00:00	\N	11
381	2013	3	2013-01-16 00:00:00	\N	12
382	2013	4	2013-01-17 00:00:00	\N	13
383	2013	5	2013-01-18 00:00:00	\N	14
384	2013	6	2013-01-19 00:00:00	\N	0
385	2013	0	2013-01-20 00:00:00	\N	0
386	2013	1	2013-01-21 00:00:00	\N	15
387	2013	2	2013-01-22 00:00:00	\N	16
388	2013	3	2013-01-23 00:00:00	\N	17
389	2013	4	2013-01-24 00:00:00	\N	18
390	2013	5	2013-01-25 00:00:00	\N	19
391	2013	6	2013-01-26 00:00:00	\N	0
392	2013	0	2013-01-27 00:00:00	\N	0
393	2013	1	2013-01-28 00:00:00	\N	20
394	2013	2	2013-01-29 00:00:00	\N	21
395	2013	3	2013-01-30 00:00:00	\N	22
396	2013	4	2013-01-31 00:00:00	\N	23
397	2013	5	2013-02-01 00:00:00	\N	24
317	2014	4	2014-11-13 00:00:00	\N	224
318	2014	5	2014-11-14 00:00:00	\N	225
321	2014	1	2014-11-17 00:00:00	\N	226
322	2014	2	2014-11-18 00:00:00	\N	227
323	2014	3	2014-11-19 00:00:00	\N	228
359	2014	4	2014-12-25 00:00:00	\N	0
398	2013	6	2013-02-02 00:00:00	\N	0
399	2013	0	2013-02-03 00:00:00	\N	0
400	2013	1	2013-02-04 00:00:00	\N	25
401	2013	2	2013-02-05 00:00:00	\N	26
78	2014	3	2014-03-19 00:00:00	\N	53
98	2014	2	2014-04-08 00:00:00	\N	67
118	2014	1	2014-04-28 00:00:00	\N	81
136	2014	5	2014-05-16 00:00:00	\N	95
146	2014	1	2014-05-26 00:00:00	\N	101
163	2014	4	2014-06-12 00:00:00	\N	114
183	2014	3	2014-07-02 00:00:00	\N	128
203	2014	2	2014-07-22 00:00:00	\N	142
223	2014	1	2014-08-11 00:00:00	\N	156
241	2014	5	2014-08-29 00:00:00	\N	170
261	2014	4	2014-09-18 00:00:00	\N	184
281	2014	3	2014-10-08 00:00:00	\N	198
293	2014	1	2014-10-20 00:00:00	\N	206
294	2014	2	2014-10-21 00:00:00	\N	207
295	2014	3	2014-10-22 00:00:00	\N	208
296	2014	4	2014-10-23 00:00:00	\N	209
297	2014	5	2014-10-24 00:00:00	\N	210
300	2014	1	2014-10-27 00:00:00	\N	211
301	2014	2	2014-10-28 00:00:00	\N	212
302	2014	3	2014-10-29 00:00:00	\N	213
303	2014	4	2014-10-30 00:00:00	\N	214
324	2014	4	2014-11-20 00:00:00	\N	229
325	2014	5	2014-11-21 00:00:00	\N	230
328	2014	1	2014-11-24 00:00:00	\N	231
329	2014	2	2014-11-25 00:00:00	\N	232
330	2014	3	2014-11-26 00:00:00	\N	233
331	2014	4	2014-11-27 00:00:00	\N	234
332	2014	5	2014-11-28 00:00:00	\N	235
335	2014	1	2014-12-01 00:00:00	\N	236
336	2014	2	2014-12-02 00:00:00	\N	237
337	2014	3	2014-12-03 00:00:00	\N	238
338	2014	4	2014-12-04 00:00:00	\N	239
339	2014	5	2014-12-05 00:00:00	\N	240
342	2014	1	2014-12-08 00:00:00	\N	241
343	2014	2	2014-12-09 00:00:00	\N	242
344	2014	3	2014-12-10 00:00:00	\N	243
345	2014	4	2014-12-11 00:00:00	\N	244
346	2014	5	2014-12-12 00:00:00	\N	245
349	2014	1	2014-12-15 00:00:00	\N	246
350	2014	2	2014-12-16 00:00:00	\N	247
351	2014	3	2014-12-17 00:00:00	\N	248
352	2014	4	2014-12-18 00:00:00	\N	249
353	2014	5	2014-12-19 00:00:00	\N	250
402	2013	3	2013-02-06 00:00:00	\N	27
403	2013	4	2013-02-07 00:00:00	\N	28
404	2013	5	2013-02-08 00:00:00	\N	29
405	2013	6	2013-02-09 00:00:00	\N	0
406	2013	0	2013-02-10 00:00:00	\N	0
407	2013	1	2013-02-11 00:00:00	\N	30
408	2013	2	2013-02-12 00:00:00	\N	31
409	2013	3	2013-02-13 00:00:00	\N	32
410	2013	4	2013-02-14 00:00:00	\N	33
411	2013	5	2013-02-15 00:00:00	\N	34
412	2013	6	2013-02-16 00:00:00	\N	0
413	2013	0	2013-02-17 00:00:00	\N	0
414	2013	1	2013-02-18 00:00:00	\N	35
415	2013	2	2013-02-19 00:00:00	\N	36
416	2013	3	2013-02-20 00:00:00	\N	37
417	2013	4	2013-02-21 00:00:00	\N	38
418	2013	5	2013-02-22 00:00:00	\N	39
419	2013	6	2013-02-23 00:00:00	\N	0
420	2013	0	2013-02-24 00:00:00	\N	0
421	2013	1	2013-02-25 00:00:00	\N	40
422	2013	2	2013-02-26 00:00:00	\N	41
423	2013	3	2013-02-27 00:00:00	\N	42
424	2013	4	2013-02-28 00:00:00	\N	43
425	2013	5	2013-03-01 00:00:00	\N	44
426	2013	6	2013-03-02 00:00:00	\N	0
427	2013	0	2013-03-03 00:00:00	\N	0
428	2013	1	2013-03-04 00:00:00	\N	45
429	2013	2	2013-03-05 00:00:00	\N	46
430	2013	3	2013-03-06 00:00:00	\N	47
431	2013	4	2013-03-07 00:00:00	\N	48
432	2013	5	2013-03-08 00:00:00	\N	49
433	2013	6	2013-03-09 00:00:00	\N	0
434	2013	0	2013-03-10 00:00:00	\N	0
435	2013	1	2013-03-11 00:00:00	\N	50
436	2013	2	2013-03-12 00:00:00	\N	51
437	2013	3	2013-03-13 00:00:00	\N	52
438	2013	4	2013-03-14 00:00:00	\N	53
439	2013	5	2013-03-15 00:00:00	\N	54
440	2013	6	2013-03-16 00:00:00	\N	0
441	2013	0	2013-03-17 00:00:00	\N	0
442	2013	1	2013-03-18 00:00:00	\N	55
443	2013	2	2013-03-19 00:00:00	\N	56
444	2013	3	2013-03-20 00:00:00	\N	57
445	2013	4	2013-03-21 00:00:00	\N	58
446	2013	5	2013-03-22 00:00:00	\N	59
447	2013	6	2013-03-23 00:00:00	\N	0
448	2013	0	2013-03-24 00:00:00	\N	0
449	2013	1	2013-03-25 00:00:00	\N	60
450	2013	2	2013-03-26 00:00:00	\N	61
451	2013	3	2013-03-27 00:00:00	\N	62
452	2013	4	2013-03-28 00:00:00	\N	63
453	2013	5	2013-03-29 00:00:00	\N	64
454	2013	6	2013-03-30 00:00:00	\N	0
455	2013	0	2013-03-31 00:00:00	\N	0
456	2013	1	2013-04-01 00:00:00	\N	65
457	2013	2	2013-04-02 00:00:00	\N	66
458	2013	3	2013-04-03 00:00:00	\N	67
459	2013	4	2013-04-04 00:00:00	\N	68
460	2013	5	2013-04-05 00:00:00	\N	69
461	2013	6	2013-04-06 00:00:00	\N	0
462	2013	0	2013-04-07 00:00:00	\N	0
463	2013	1	2013-04-08 00:00:00	\N	70
464	2013	2	2013-04-09 00:00:00	\N	71
465	2013	3	2013-04-10 00:00:00	\N	72
466	2013	4	2013-04-11 00:00:00	\N	73
467	2013	5	2013-04-12 00:00:00	\N	74
468	2013	6	2013-04-13 00:00:00	\N	0
469	2013	0	2013-04-14 00:00:00	\N	0
470	2013	1	2013-04-15 00:00:00	\N	75
471	2013	2	2013-04-16 00:00:00	\N	76
472	2013	3	2013-04-17 00:00:00	\N	77
473	2013	4	2013-04-18 00:00:00	\N	78
474	2013	5	2013-04-19 00:00:00	\N	79
475	2013	6	2013-04-20 00:00:00	\N	0
476	2013	0	2013-04-21 00:00:00	\N	0
477	2013	1	2013-04-22 00:00:00	\N	80
478	2013	2	2013-04-23 00:00:00	\N	81
479	2013	3	2013-04-24 00:00:00	\N	82
480	2013	4	2013-04-25 00:00:00	\N	83
481	2013	5	2013-04-26 00:00:00	\N	84
482	2013	6	2013-04-27 00:00:00	\N	0
483	2013	0	2013-04-28 00:00:00	\N	0
484	2013	1	2013-04-29 00:00:00	\N	85
485	2013	2	2013-04-30 00:00:00	\N	86
486	2013	3	2013-05-01 00:00:00	\N	87
487	2013	4	2013-05-02 00:00:00	\N	88
488	2013	5	2013-05-03 00:00:00	\N	89
489	2013	6	2013-05-04 00:00:00	\N	0
490	2013	0	2013-05-05 00:00:00	\N	0
491	2013	1	2013-05-06 00:00:00	\N	90
492	2013	2	2013-05-07 00:00:00	\N	91
493	2013	3	2013-05-08 00:00:00	\N	92
494	2013	4	2013-05-09 00:00:00	\N	93
495	2013	5	2013-05-10 00:00:00	\N	94
496	2013	6	2013-05-11 00:00:00	\N	0
497	2013	0	2013-05-12 00:00:00	\N	0
498	2013	1	2013-05-13 00:00:00	\N	95
499	2013	2	2013-05-14 00:00:00	\N	96
500	2013	3	2013-05-15 00:00:00	\N	97
501	2013	4	2013-05-16 00:00:00	\N	98
502	2013	5	2013-05-17 00:00:00	\N	99
503	2013	6	2013-05-18 00:00:00	\N	0
504	2013	0	2013-05-19 00:00:00	\N	0
505	2013	1	2013-05-20 00:00:00	\N	100
506	2013	2	2013-05-21 00:00:00	\N	101
507	2013	3	2013-05-22 00:00:00	\N	102
508	2013	4	2013-05-23 00:00:00	\N	103
509	2013	5	2013-05-24 00:00:00	\N	104
510	2013	6	2013-05-25 00:00:00	\N	0
511	2013	0	2013-05-26 00:00:00	\N	0
512	2013	1	2013-05-27 00:00:00	\N	105
513	2013	2	2013-05-28 00:00:00	\N	106
514	2013	3	2013-05-29 00:00:00	\N	107
515	2013	4	2013-05-30 00:00:00	\N	108
516	2013	5	2013-05-31 00:00:00	\N	109
517	2013	6	2013-06-01 00:00:00	\N	0
518	2013	0	2013-06-02 00:00:00	\N	0
519	2013	1	2013-06-03 00:00:00	\N	110
520	2013	2	2013-06-04 00:00:00	\N	111
521	2013	3	2013-06-05 00:00:00	\N	112
522	2013	4	2013-06-06 00:00:00	\N	113
523	2013	5	2013-06-07 00:00:00	\N	114
524	2013	6	2013-06-08 00:00:00	\N	0
525	2013	0	2013-06-09 00:00:00	\N	0
526	2013	1	2013-06-10 00:00:00	\N	115
527	2013	2	2013-06-11 00:00:00	\N	116
528	2013	3	2013-06-12 00:00:00	\N	117
529	2013	4	2013-06-13 00:00:00	\N	118
530	2013	5	2013-06-14 00:00:00	\N	119
531	2013	6	2013-06-15 00:00:00	\N	0
532	2013	0	2013-06-16 00:00:00	\N	0
533	2013	1	2013-06-17 00:00:00	\N	120
534	2013	2	2013-06-18 00:00:00	\N	121
535	2013	3	2013-06-19 00:00:00	\N	122
536	2013	4	2013-06-20 00:00:00	\N	123
537	2013	5	2013-06-21 00:00:00	\N	124
538	2013	6	2013-06-22 00:00:00	\N	0
539	2013	0	2013-06-23 00:00:00	\N	0
540	2013	1	2013-06-24 00:00:00	\N	125
541	2013	2	2013-06-25 00:00:00	\N	126
542	2013	3	2013-06-26 00:00:00	\N	127
543	2013	4	2013-06-27 00:00:00	\N	128
544	2013	5	2013-06-28 00:00:00	\N	129
545	2013	6	2013-06-29 00:00:00	\N	0
546	2013	0	2013-06-30 00:00:00	\N	0
547	2013	1	2013-07-01 00:00:00	\N	130
548	2013	2	2013-07-02 00:00:00	\N	131
549	2013	3	2013-07-03 00:00:00	\N	132
550	2013	4	2013-07-04 00:00:00	\N	133
551	2013	5	2013-07-05 00:00:00	\N	134
552	2013	6	2013-07-06 00:00:00	\N	0
553	2013	0	2013-07-07 00:00:00	\N	0
554	2013	1	2013-07-08 00:00:00	\N	135
555	2013	2	2013-07-09 00:00:00	\N	136
556	2013	3	2013-07-10 00:00:00	\N	137
557	2013	4	2013-07-11 00:00:00	\N	138
558	2013	5	2013-07-12 00:00:00	\N	139
559	2013	6	2013-07-13 00:00:00	\N	0
560	2013	0	2013-07-14 00:00:00	\N	0
561	2013	1	2013-07-15 00:00:00	\N	140
562	2013	2	2013-07-16 00:00:00	\N	141
563	2013	3	2013-07-17 00:00:00	\N	142
564	2013	4	2013-07-18 00:00:00	\N	143
565	2013	5	2013-07-19 00:00:00	\N	144
566	2013	6	2013-07-20 00:00:00	\N	0
567	2013	0	2013-07-21 00:00:00	\N	0
568	2013	1	2013-07-22 00:00:00	\N	145
569	2013	2	2013-07-23 00:00:00	\N	146
570	2013	3	2013-07-24 00:00:00	\N	147
571	2013	4	2013-07-25 00:00:00	\N	148
572	2013	5	2013-07-26 00:00:00	\N	149
573	2013	6	2013-07-27 00:00:00	\N	0
574	2013	0	2013-07-28 00:00:00	\N	0
575	2013	1	2013-07-29 00:00:00	\N	150
576	2013	2	2013-07-30 00:00:00	\N	151
577	2013	3	2013-07-31 00:00:00	\N	152
578	2013	4	2013-08-01 00:00:00	\N	153
579	2013	5	2013-08-02 00:00:00	\N	154
580	2013	6	2013-08-03 00:00:00	\N	0
581	2013	0	2013-08-04 00:00:00	\N	0
582	2013	1	2013-08-05 00:00:00	\N	155
583	2013	2	2013-08-06 00:00:00	\N	156
584	2013	3	2013-08-07 00:00:00	\N	157
585	2013	4	2013-08-08 00:00:00	\N	158
586	2013	5	2013-08-09 00:00:00	\N	159
587	2013	6	2013-08-10 00:00:00	\N	0
588	2013	0	2013-08-11 00:00:00	\N	0
589	2013	1	2013-08-12 00:00:00	\N	160
590	2013	2	2013-08-13 00:00:00	\N	161
591	2013	3	2013-08-14 00:00:00	\N	162
592	2013	4	2013-08-15 00:00:00	\N	163
593	2013	5	2013-08-16 00:00:00	\N	164
594	2013	6	2013-08-17 00:00:00	\N	0
595	2013	0	2013-08-18 00:00:00	\N	0
596	2013	1	2013-08-19 00:00:00	\N	165
597	2013	2	2013-08-20 00:00:00	\N	166
598	2013	3	2013-08-21 00:00:00	\N	167
599	2013	4	2013-08-22 00:00:00	\N	168
600	2013	5	2013-08-23 00:00:00	\N	169
601	2013	6	2013-08-24 00:00:00	\N	0
602	2013	0	2013-08-25 00:00:00	\N	0
603	2013	1	2013-08-26 00:00:00	\N	170
604	2013	2	2013-08-27 00:00:00	\N	171
605	2013	3	2013-08-28 00:00:00	\N	172
606	2013	4	2013-08-29 00:00:00	\N	173
607	2013	5	2013-08-30 00:00:00	\N	174
608	2013	6	2013-08-31 00:00:00	\N	0
609	2013	0	2013-09-01 00:00:00	\N	0
610	2013	1	2013-09-02 00:00:00	\N	175
611	2013	2	2013-09-03 00:00:00	\N	176
612	2013	3	2013-09-04 00:00:00	\N	177
613	2013	4	2013-09-05 00:00:00	\N	178
614	2013	5	2013-09-06 00:00:00	\N	179
615	2013	6	2013-09-07 00:00:00	\N	0
616	2013	0	2013-09-08 00:00:00	\N	0
617	2013	1	2013-09-09 00:00:00	\N	180
618	2013	2	2013-09-10 00:00:00	\N	181
619	2013	3	2013-09-11 00:00:00	\N	182
620	2013	4	2013-09-12 00:00:00	\N	183
621	2013	5	2013-09-13 00:00:00	\N	184
622	2013	6	2013-09-14 00:00:00	\N	0
623	2013	0	2013-09-15 00:00:00	\N	0
624	2013	1	2013-09-16 00:00:00	\N	185
625	2013	2	2013-09-17 00:00:00	\N	186
626	2013	3	2013-09-18 00:00:00	\N	187
627	2013	4	2013-09-19 00:00:00	\N	188
628	2013	5	2013-09-20 00:00:00	\N	189
629	2013	6	2013-09-21 00:00:00	\N	0
630	2013	0	2013-09-22 00:00:00	\N	0
631	2013	1	2013-09-23 00:00:00	\N	190
632	2013	2	2013-09-24 00:00:00	\N	191
633	2013	3	2013-09-25 00:00:00	\N	192
634	2013	4	2013-09-26 00:00:00	\N	193
635	2013	5	2013-09-27 00:00:00	\N	194
636	2013	6	2013-09-28 00:00:00	\N	0
637	2013	0	2013-09-29 00:00:00	\N	0
638	2013	1	2013-09-30 00:00:00	\N	195
639	2013	2	2013-10-01 00:00:00	\N	196
640	2013	3	2013-10-02 00:00:00	\N	197
641	2013	4	2013-10-03 00:00:00	\N	198
642	2013	5	2013-10-04 00:00:00	\N	199
643	2013	6	2013-10-05 00:00:00	\N	0
644	2013	0	2013-10-06 00:00:00	\N	0
645	2013	1	2013-10-07 00:00:00	\N	200
646	2013	2	2013-10-08 00:00:00	\N	201
647	2013	3	2013-10-09 00:00:00	\N	202
648	2013	4	2013-10-10 00:00:00	\N	203
649	2013	5	2013-10-11 00:00:00	\N	204
650	2013	6	2013-10-12 00:00:00	\N	0
651	2013	0	2013-10-13 00:00:00	\N	0
652	2013	1	2013-10-14 00:00:00	\N	205
653	2013	2	2013-10-15 00:00:00	\N	206
654	2013	3	2013-10-16 00:00:00	\N	207
655	2013	4	2013-10-17 00:00:00	\N	208
656	2013	5	2013-10-18 00:00:00	\N	209
657	2013	6	2013-10-19 00:00:00	\N	0
658	2013	0	2013-10-20 00:00:00	\N	0
659	2013	1	2013-10-21 00:00:00	\N	210
660	2013	2	2013-10-22 00:00:00	\N	211
661	2013	3	2013-10-23 00:00:00	\N	212
662	2013	4	2013-10-24 00:00:00	\N	213
663	2013	5	2013-10-25 00:00:00	\N	214
664	2013	6	2013-10-26 00:00:00	\N	0
665	2013	0	2013-10-27 00:00:00	\N	0
666	2013	1	2013-10-28 00:00:00	\N	215
667	2013	2	2013-10-29 00:00:00	\N	216
668	2013	3	2013-10-30 00:00:00	\N	217
669	2013	4	2013-10-31 00:00:00	\N	218
670	2013	5	2013-11-01 00:00:00	\N	219
671	2013	6	2013-11-02 00:00:00	\N	0
672	2013	0	2013-11-03 00:00:00	\N	0
673	2013	1	2013-11-04 00:00:00	\N	220
674	2013	2	2013-11-05 00:00:00	\N	221
675	2013	3	2013-11-06 00:00:00	\N	222
676	2013	4	2013-11-07 00:00:00	\N	223
677	2013	5	2013-11-08 00:00:00	\N	224
678	2013	6	2013-11-09 00:00:00	\N	0
679	2013	0	2013-11-10 00:00:00	\N	0
680	2013	1	2013-11-11 00:00:00	\N	225
681	2013	2	2013-11-12 00:00:00	\N	226
682	2013	3	2013-11-13 00:00:00	\N	227
683	2013	4	2013-11-14 00:00:00	\N	228
684	2013	5	2013-11-15 00:00:00	\N	229
685	2013	6	2013-11-16 00:00:00	\N	0
686	2013	0	2013-11-17 00:00:00	\N	0
687	2013	1	2013-11-18 00:00:00	\N	230
688	2013	2	2013-11-19 00:00:00	\N	231
689	2013	3	2013-11-20 00:00:00	\N	232
690	2013	4	2013-11-21 00:00:00	\N	233
691	2013	5	2013-11-22 00:00:00	\N	234
692	2013	6	2013-11-23 00:00:00	\N	0
693	2013	0	2013-11-24 00:00:00	\N	0
694	2013	1	2013-11-25 00:00:00	\N	235
695	2013	2	2013-11-26 00:00:00	\N	236
696	2013	3	2013-11-27 00:00:00	\N	237
697	2013	4	2013-11-28 00:00:00	\N	238
698	2013	5	2013-11-29 00:00:00	\N	239
699	2013	6	2013-11-30 00:00:00	\N	0
700	2013	0	2013-12-01 00:00:00	\N	0
701	2013	1	2013-12-02 00:00:00	\N	240
702	2013	2	2013-12-03 00:00:00	\N	241
703	2013	3	2013-12-04 00:00:00	\N	242
704	2013	4	2013-12-05 00:00:00	\N	243
705	2013	5	2013-12-06 00:00:00	\N	244
706	2013	6	2013-12-07 00:00:00	\N	0
707	2013	0	2013-12-08 00:00:00	\N	0
708	2013	1	2013-12-09 00:00:00	\N	245
709	2013	2	2013-12-10 00:00:00	\N	246
710	2013	3	2013-12-11 00:00:00	\N	247
711	2013	4	2013-12-12 00:00:00	\N	248
712	2013	5	2013-12-13 00:00:00	\N	249
713	2013	6	2013-12-14 00:00:00	\N	0
714	2013	0	2013-12-15 00:00:00	\N	0
715	2013	1	2013-12-16 00:00:00	\N	250
716	2013	2	2013-12-17 00:00:00	\N	251
717	2013	3	2013-12-18 00:00:00	\N	252
718	2013	4	2013-12-19 00:00:00	\N	253
719	2013	5	2013-12-20 00:00:00	\N	254
720	2013	6	2013-12-21 00:00:00	\N	0
721	2013	0	2013-12-22 00:00:00	\N	0
722	2013	1	2013-12-23 00:00:00	\N	255
723	2013	2	2013-12-24 00:00:00	\N	256
724	2013	3	2013-12-25 00:00:00	\N	257
725	2013	4	2013-12-26 00:00:00	\N	258
726	2013	5	2013-12-27 00:00:00	\N	259
727	2013	6	2013-12-28 00:00:00	\N	0
728	2013	0	2013-12-29 00:00:00	\N	0
729	2013	1	2013-12-30 00:00:00	\N	260
730	2013	2	2013-12-31 00:00:00	\N	261
\.


--
-- TOC entry 2327 (class 0 OID 0)
-- Dependencies: 174
-- Name: ddlb_ddlb__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ddlb_ddlb__id_seq', 730, true);


--
-- TOC entry 2215 (class 0 OID 104007)
-- Dependencies: 175
-- Data for Name: dpto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY dpto (dpto__id, tpdp__id, dptopdre, dptocdgo, dptodscr, dptotelf, dptoextn, dptodire, dptoetdo) FROM stdin;
11	1	\N	PP	PREFECTURA PROVINCIAL	\N	\N	\N	
20	1	\N	NA	Sin departamento	\N	\N	\N	I
\.


--
-- TOC entry 2328 (class 0 OID 0)
-- Dependencies: 176
-- Name: dpto_dpto__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('dpto_dpto__id_seq', 20, true);


--
-- TOC entry 2217 (class 0 OID 104012)
-- Dependencies: 177
-- Data for Name: edtr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY edtr (edtr__id, edtrcdgo, edtrdscr) FROM stdin;
1	E001	BORRADOR
2	E002	REVISADO
3	E003	ENVIADO
4	E004	RECIBIDO
5	E005	ARCHIVADO
6	EX01	APROBADO
7	EX02	NEGADO
8	EX03	PENDIENTE
9	E006	ANULADO
10	E007	ENVIADO AL JEFE
\.


--
-- TOC entry 2329 (class 0 OID 0)
-- Dependencies: 178
-- Name: edtr_edtr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('edtr_edtr__id_seq', 10, true);


--
-- TOC entry 2219 (class 0 OID 104017)
-- Dependencies: 179
-- Data for Name: logf; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY logf (logf__id, logfcaus, logferro, logffcha, logf_url, logfusro) FROM stdin;
613	Cannot invoke method refresh() on null object	Cannot invoke method refresh() on null object	2014-03-19 15:05:35.502	/happy/persona/cargarUsuariosLdap	63
614	No signature of method: [Ljava.lang.String;.get() is applicable for argument types: (java.lang.Integer) values: [1]\nPossible solutions: getAt(java.lang.Integer), grep(), grep(), getAt(java.lang.String), getAt(groovy.lang.IntRange), grep(java.lang.Object)	No signature of method: [Ljava.lang.String;.get() is applicable for argument types: (java.lang.Integer) values: [1]\nPossible solutions: getAt(java.lang.Integer), grep(), grep(), getAt(java.lang.String), getAt(groovy.lang.IntRange), grep(java.lang.Object)	2014-03-19 15:08:50.841	/happy/persona/cargarUsuariosLdap	63
\.


--
-- TOC entry 2330 (class 0 OID 0)
-- Dependencies: 180
-- Name: logf_logf__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('logf_logf__id_seq', 614, true);


--
-- TOC entry 2221 (class 0 OID 104025)
-- Dependencies: 181
-- Data for Name: mdlo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mdlo (mdlo__id, mdlonmbr, mdlodscr, mdloordn) FROM stdin;
0	noAsignado	noAsignado	99
1	Inicio	Inicio	1
3	Administración	Administración	2
2	Trámites	Trámites	3
\.


--
-- TOC entry 2331 (class 0 OID 0)
-- Dependencies: 182
-- Name: mdlo_mdlo__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('mdlo_mdlo__id_seq', 3, true);


--
-- TOC entry 2223 (class 0 OID 104030)
-- Dependencies: 183
-- Data for Name: nmro; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY nmro (nmro__id, dpto__id, tpdc__id, nmrovlor) FROM stdin;
\.


--
-- TOC entry 2332 (class 0 OID 0)
-- Dependencies: 184
-- Name: nmro_nmro__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('nmro_nmro__id_seq', 1, false);


--
-- TOC entry 2225 (class 0 OID 104035)
-- Dependencies: 185
-- Data for Name: obtr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY obtr (obtr__id, trmt__id, prsn__id, obtrfcha, obtrobsr, obtrtipo) FROM stdin;
\.


--
-- TOC entry 2333 (class 0 OID 0)
-- Dependencies: 186
-- Name: obtr_obtr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('obtr_obtr__id_seq', 9, true);


--
-- TOC entry 2227 (class 0 OID 104043)
-- Dependencies: 187
-- Data for Name: orgn; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY orgn (orgn__id, tppr__id, orgncdla, orgnfcha, orgnnmbr, orgnnbct, orgnapct, prsntitl, orgncrgo, orgnmail, orgntelf) FROM stdin;
1	2	1715068159	\N	nombre	nombre contacto	apellido contacto	titu	cargo	email@mail.com	1234567
\.


--
-- TOC entry 2334 (class 0 OID 0)
-- Dependencies: 188
-- Name: orgn_orgn__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('orgn_orgn__id_seq', 1, true);


--
-- TOC entry 2229 (class 0 OID 104048)
-- Dependencies: 189
-- Data for Name: perm; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY perm (perm__id, permcdgo, permdscr) FROM stdin;
1	P001	DIRECTOR
2	P002	JEFE
4	P004	VER
5	P005	AUTOREVISADO
6	P006	TRAMITAR
7	P007	PLAZO
8	P008	REDIRECCIONAR
9	P009	ANULAR
10	P010	RECIBIR
11	P011	ARCHIVAR
12	P012	REACTIVAR
13	P013	ADMINISTRACION
3	E001	RECEPCION
15	E002	ENVIAR
14	P014	REGISTRO DE DATOS
16	P015	EXTERNOS
\.


--
-- TOC entry 2335 (class 0 OID 0)
-- Dependencies: 190
-- Name: perm_perm__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('perm_perm__id_seq', 15, true);


--
-- TOC entry 2231 (class 0 OID 104053)
-- Dependencies: 191
-- Data for Name: prfl; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY prfl (prfl__id, prflpdre, prflnmbr, prfldscr, prflobsr, prflcdgo) FROM stdin;
1	\N	ADMINISTRADOR	ADMINISTRADOR	\N	ADM
3	\N	USUARIO	USUARIO	\N	USU
\.


--
-- TOC entry 2336 (class 0 OID 0)
-- Dependencies: 192
-- Name: prfl_prfl__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('prfl_prfl__id_seq', 3, true);


--
-- TOC entry 2233 (class 0 OID 104058)
-- Dependencies: 193
-- Data for Name: prms; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY prms (prms__id, prfl__id, accn__id) FROM stdin;
2	1	179
3	1	246
4	1	271
5	1	36
6	1	264
7	1	132
8	1	275
9	1	281
10	1	270
11	1	347
12	1	320
13	1	311
14	3	264
15	3	132
16	3	271
17	3	281
18	3	270
19	3	347
20	3	320
21	3	311
\.


--
-- TOC entry 2337 (class 0 OID 0)
-- Dependencies: 194
-- Name: prms_prms__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('prms_prms__id_seq', 21, true);


--
-- TOC entry 2235 (class 0 OID 104063)
-- Dependencies: 195
-- Data for Name: prsn; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY prsn (prsn__id, dpto__id, prsncdla, prsnnmbr, prsnapll, prsnfcna, prsnfcin, prsnfcfn, prsnsgla, prsntitl, prsncrgo, prsnmail, prsnlogn, prsnpass, prsnactv, prsnatrz, prsnfcps, prsntelf, prsnjefe, prsntfcl, prsnfoto, prsncdgo) FROM stdin;
1522	20	\N	LUIS EDUARDO	PEÑAFIEL BARREZUETA	\N	\N	\N	\N	\N	\N	lpenafiel@pichincha.gob.ec	lpenafiel	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1945	20	\N	María Isabel	Cruz	\N	\N	\N	\N	\N	\N	micruz@pichincha.gob.ec	micruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
63	11	1715068159	ADMIN	PRUEBAS	\N	\N	\N	AP	ING	\N	LUZMA_871@YAHOO.COM	admin	202cb962ac59075b964b07152d234b70	1	21232f297a57a5a743894a0e4a801fc3	2014-03-26	\N	0	\N	63.png	\N
1464	20	\N	Margarita Elisabeth	Espinosa Burbano	\N	\N	\N	\N	\N	\N	mespinosa@pichincha.gob.ec	mespinosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1465	20	\N	CHRISTIAN FABIAN	MOLINA VIEIRA	\N	\N	\N	\N	\N	\N	cmolina@pichincha.gob.ec	cmolina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1466	20	\N	Jorge Santiago	Cáceres Flores	\N	\N	\N	\N	\N	\N	scaceres@pichincha.gob.ec	scaceres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1467	20	\N	Edgar Paúl	Cantuña Logacho	\N	\N	\N	\N	\N	\N	pcantuna@pichincha.gob.ec	pcantuna	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1468	20	\N	Aida Mercedes	Baca Balladares	\N	\N	\N	\N	\N	\N	abaca@pichincha.gob.ec	abaca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1469	20	\N	Amada Jaqueline	Lopez Mejia	\N	\N	\N	\N	\N	\N	ajlopez@pichincha.gob.ec	ajlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1470	20	\N	Natalia Raquel	Vela Poveda	\N	\N	\N	\N	\N	\N	nvela@pichincha.gob.ec	nvela	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1471	20	\N	José Remigio	Yunda Buenaño	\N	\N	\N	\N	\N	\N	jyunda@pichincha.gob.ec	jyunda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1472	20	\N	Maria del	Carmen Vásconez Conrado	\N	\N	\N	\N	\N	\N	mvasconez@pichincha.gob.ec	mvasconez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1473	20	\N	Rosa Elisabeth	Rubio Moreno	\N	\N	\N	\N	\N	\N	erubio@pichincha.gob.ec	erubio	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1474	20	\N	Anita Isabel	Chalco Paredes	\N	\N	\N	\N	\N	\N	achalco@pichincha.gob.ec	achalco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1475	20	\N	Loiva Soraya	Gamez Lopez	\N	\N	\N	\N	\N	\N	sgamez@pichincha.gob.ec	sgamez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1476	20	\N	Jimmy José	Bustamante Bustamante	\N	\N	\N	\N	\N	\N	jbustamante@pichincha.gob.ec	jbustamante	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1477	20	\N	CARLOS ALBERTO	CARCELEN BENITEZ	\N	\N	\N	\N	\N	\N	ccarcelen@pichincha.gob.ec	ccarcelen	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1478	20	\N	HECTOR DANILO	RODRIGUEZ GRANJA	\N	\N	\N	\N	\N	\N	drodriguez@pichincha.gob.ec	drodriguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1479	20	\N	FRANCISCO JAVIER	GARCIA MOLINA	\N	\N	\N	\N	\N	\N	garciaf@pichincha.gob.ec	garciaf	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1480	20	\N	GABRIELA FERNADA	ROMERO ORELLANA	\N	\N	\N	\N	\N	\N	gromero@pichincha.gob.ec	gromero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1481	20	\N	LAURA MARCELA	COSTALES PEÑAHERRERA	\N	\N	\N	\N	\N	\N	mcostales@pichincha.gob.ec	mcostales	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1482	20	\N	GONZALO JAVIER	ARAUJO SANCHEZ	\N	\N	\N	\N	\N	\N	garaujo@pichincha.gob.ec	garaujo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1483	20	\N	GIOVANI FRANCISCO	CAMACHO JACOME	\N	\N	\N	\N	\N	\N	gcamacho@pichincha.gob.ec	gcamacho	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1484	20	\N	LILIA DEL	VALLE ALLAN RAMOS	\N	\N	\N	\N	\N	\N	lallan@pichincha.gob.ec	lallan	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1485	20	\N	MARIANA ROCIO	ACOSTA SILVA	\N	\N	\N	\N	\N	\N	racosta@pichincha.gob.ec	racosta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1486	20	\N	OLGA EULALIA	COBOS GARCES	\N	\N	\N	\N	\N	\N	ocobos@pichincha.gob.ec	ocobos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1487	20	\N	LEONOR ALEXANDRA	BURI VILLACRES	\N	\N	\N	\N	\N	\N	lburi@pichincha.gob.ec	lburi	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1488	20	\N	SONIA ELIZABETH	BENITEZ CARRILLO	\N	\N	\N	\N	\N	\N	sbenitez@pichincha.gob.ec	sbenitez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1489	20	\N	DIEGO MANUEL	ESPINOSA D'HERBECOURTH	\N	\N	\N	\N	\N	\N	despinosa@pichincha.gob.ec	despinosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1490	20	\N	ANGEL OSWALDO	GUEVARA GUEVAR	\N	\N	\N	\N	\N	\N	oguevara@pichincha.gob.ec	oguevara	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1491	20	\N	FRANKLIN DANIEL	BURBANO VELAZCO	\N	\N	\N	\N	\N	\N	fburbano@pichincha.gob.ec	fburbano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1492	20	\N	GALO VINICIO	HARO AGUIAR	\N	\N	\N	\N	\N	\N	gharo@pichincha.gob.ec	gharo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1493	20	\N	JANETH ELIZABETH	PEREZ ONOFA	\N	\N	\N	\N	\N	\N	jperezo@pichincha.gob.ec	jperezo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1494	20	\N	JHOSSEHETT VICTORIA	VILLACIS TIVAN	\N	\N	\N	\N	\N	\N	jvillacis@pichincha.gob.ec	jvillacis	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1495	20	\N	GUILLERMO FERNANDO	MONTENEGRO ARMAS	\N	\N	\N	\N	\N	\N	gmontenegro@pichincha.gob.ec	gmontenegro	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1496	20	\N	OLDRA LORENA	BELTRAN CISNEROS	\N	\N	\N	\N	\N	\N	lbeltran@pichincha.gob.ec	lbeltran	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1497	20	\N	DAVID FRANCISCO	RODRIGUEZ MOREANO	\N	\N	\N	\N	\N	\N	dfrodriguez@pichincha.gob.ec	dfrodriguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1498	20	\N	WILSON RAUL	ILLESCAS LEON	\N	\N	\N	\N	\N	\N	rillescas@pichincha.gob.ec	rillescas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1499	20	\N	ALEX ALBERTO	RUBIO NIETO	\N	\N	\N	\N	\N	\N	arubio@pichincha.gob.ec	arubio	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1500	20	\N	FLOR ALBA	ASIPUELA VASCO	\N	\N	\N	\N	\N	\N	fasipuela@pichincha.gob.ec	fasipuela	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1501	20	\N	ALONSO GUSTAVO	ANDRADE AMOROSO	\N	\N	\N	\N	\N	\N	aandrade@pichincha.gob.ec	aandrade	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1502	20	\N	MARIA EDITH	SANCHEZ ROMERO	\N	\N	\N	\N	\N	\N	mesanchez@pichincha.gob.ec	mesanchez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1503	20	\N	BLANCA TULA	ARCENTALES BARRIGA	\N	\N	\N	\N	\N	\N	barcentales@pichincha.gob.ec	barcentales	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1504	20	\N	YOHANNA MARIA	MATOS BARRE	\N	\N	\N	\N	\N	\N	jmattos@pichincha.gob.ec	jmattos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1505	20	\N	CESAR MANUEL	VARGAS HIDALGO	\N	\N	\N	\N	\N	\N	cvargas@pichincha.gob.ec	cvargas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1506	20	\N	FAUSTO SALOMON	HERNANDEZ CAJAS	\N	\N	\N	\N	\N	\N	ffernandez@pichincha.gob.ec	ffernandez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1507	20	\N	KLEVER VICENTE	MINDA NARVAEZ	\N	\N	\N	\N	\N	\N	kminda@pichincha.gob.ec	kminda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1508	20	\N	PEDRO EVARISTO	MARTINEZ CHAMBA	\N	\N	\N	\N	\N	\N	pmartinez@pichincha.gob.ec	pmartinez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1509	20	\N	LUIS HERNAN	IPIALES GUACOLLANTES	\N	\N	\N	\N	\N	\N	lipiales@pichincha.gob.ec	lipiales	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1510	20	\N	PATRICIO ENRIQUE	VINUEZA PAZ	\N	\N	\N	\N	\N	\N	pvinueza@pichincha.gob.ec	pvinueza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1511	20	\N	LUCIA MARICELA	AYALA MONTOYA	\N	\N	\N	\N	\N	\N	layala@pichincha.gob.ec	layala	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1512	20	\N	CESAR AUGUSTO	BUITRON PEREZ	\N	\N	\N	\N	\N	\N	cbuitron@pichincha.gob.ec	cbuitron	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1513	20	\N	JORGE CARLOS	ARCOS MARTINEZ	\N	\N	\N	\N	\N	\N	jarcos@pichincha.gob.ec	jarcos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1514	20	\N	JORGE ERIC	AYALA AUZ	\N	\N	\N	\N	\N	\N	eayala@pichincha.gob.ec	eayala	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1515	20	\N	ELENA GABRIELA	CAJAS CONRADO	\N	\N	\N	\N	\N	\N	ecajas@pichincha.gob.ec	ecajas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1516	20	\N	ANGEL EMILIO	CAPELO VEGA	\N	\N	\N	\N	\N	\N	acapelo@pichincha.gob.ec	acapelo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1517	20	\N	PATRICIA DEL	PILAR ESPINEL MONTUFAR	\N	\N	\N	\N	\N	\N	pespinel@pichincha.gob.ec	pespinel	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1518	20	\N	ALBA MAGDALENA	MATEHUS BECERRA	\N	\N	\N	\N	\N	\N	amatehus@pichincha.gob.ec	amatehus	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1519	20	\N	VICTOR HUGO	MOYA ACOSTA	\N	\N	\N	\N	\N	\N	vmoya@pichincha.gob.ec	vmoya	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1520	20	\N	GUADALUPE ELIZABETH	OJEDA JARAMILLO	\N	\N	\N	\N	\N	\N	gojeda@pichincha.gob.ec	gojeda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1521	20	\N	CARLOS FERNANDO	OÑA VELASQUES	\N	\N	\N	\N	\N	\N	cona@pichincha.gob.ec	cona	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1523	20	\N	WILSON MEDARDO	PEREZ CANO	\N	\N	\N	\N	\N	\N	wperez@pichincha.gob.ec	wperez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1524	20	\N	ROLANDO VICENTE	PINTADO ORDOÑEZ	\N	\N	\N	\N	\N	\N	rpintado@pichincha.gob.ec	rpintado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1525	20	\N	CARLOS HOMERO	RIVADENEIRA NARANJO	\N	\N	\N	\N	\N	\N	crivadeneira@pichincha.gob.ec	crivadeneira	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1526	20	\N	FRANCISCO PATRICIO	RIVERA DEL CASTILLO	\N	\N	\N	\N	\N	\N	frivera@pichincha.gob.ec	frivera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1527	20	\N	JUANITO MATEO	SANCHEZ CASTELLANOS	\N	\N	\N	\N	\N	\N	jsanchez@pichincha.gob.ec	jsanchez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1528	20	\N	JOSE RUPERTO	SILVA AGUILAR	\N	\N	\N	\N	\N	\N	jsilva@pichincha.gob.ec	jsilva	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1529	20	\N	ELVIA LUISA	TORRES MARQUEZ	\N	\N	\N	\N	\N	\N	etorres@pichincha.gob.ec	etorres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1530	20	\N	HUGO IVAN	VERDUGO NOVOA	\N	\N	\N	\N	\N	\N	hverdugo@pichincha.gob.ec	hverdugo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1531	20	\N	JULIA MARGARITA	MESA PONCE	\N	\N	\N	\N	\N	\N	mmesa@pichincha.gob.ec	mmesa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1532	20	\N	LORENA MARTINEZ	MARTINEZ VALENCIA	\N	\N	\N	\N	\N	\N	lmartinez@pichincha.gob.ec	lmartinez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1533	20	\N	CRISTINA JANNETH	VASQUEZ GARCIA	\N	\N	\N	\N	\N	\N	cvasquez@pichincha.gob.ec	cvasquez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1534	20	\N	JONATHAN FRANCISCO	ERAZO HARO	\N	\N	\N	\N	\N	\N	jerazo@pichincha.gob.ec	jerazo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1535	20	\N	NUMA CLEVERDAN	TOSCANO ORTIZ	\N	\N	\N	\N	\N	\N	ntoscano@pichincha.gob.ec	ntoscano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1536	20	\N	JUAN PATRICIO	CABRERA PADILLA	\N	\N	\N	\N	\N	\N	jcabrera@pichincha.gob.ec	jcabrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1537	20	\N	ANA ADELA	GARCIA VELA	\N	\N	\N	\N	\N	\N	garciaa@pichincha.gob.ec	garciaa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1538	20	\N	JESSICA PAULINA	CEVALLOS VANEGAS	\N	\N	\N	\N	\N	\N	jcevallos@pichincha.gob.ec	jcevallos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1539	20	\N	SILVIA CRISTINA	GORDON MOSQUERA	\N	\N	\N	\N	\N	\N	sgordon@pichincha.gob.ec	sgordon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1540	20	\N	JOBA MARIELA	FLORES ARIAS	\N	\N	\N	\N	\N	\N	floresm@pichincha.gob.ec	floresm	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1541	20	\N	MONICA PATRICIA	MORA VALLEJO	\N	\N	\N	\N	\N	\N	mmora@pichincha.gob.ec	mmora	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1542	20	\N	MARIA PILAR	VELA MURGEYTIO	\N	\N	\N	\N	\N	\N	mvela@pichincha.gob.ec	mvela	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1543	20	\N	EDGAR LENIN	PALACIOS BERU	\N	\N	\N	\N	\N	\N	lpalacios@pichincha.gob.ec	lpalacios	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1544	20	\N	EDWARD SERGIO	MAYORGA GAVILANEZ	\N	\N	\N	\N	\N	\N	emayorga@pichincha.gob.ec	emayorga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1545	20	\N	MAURICIO FERNANDO	VALDIVIESO MANJABE	\N	\N	\N	\N	\N	\N	mvaldivieso@pichincha.gob.ec	mvaldivieso	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1546	20	\N	MARIA DEL	PILAR VELASCO GUERRON	\N	\N	\N	\N	\N	\N	pvelasco@pichincha.gob.ec	pvelasco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1547	20	\N	PAULA ELIZABETH	ZURITA TRUJILLO	\N	\N	\N	\N	\N	\N	pzurita@pichincha.gob.ec	pzurita	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1548	20	\N	GLADYS EMELINA	JARAMILLO BUENDIA	\N	\N	\N	\N	\N	\N	gjaramil@pichincha.gob.ec	gjaramil	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1549	20	\N	ANITA LUCIA	PEDRAZA TITUAÑA	\N	\N	\N	\N	\N	\N	apedraza@pichincha.gob.ec	apedraza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1550	20	\N	CARMEN IRLANDA	DOMINGUEZ  VALVERDE	\N	\N	\N	\N	\N	\N	cdominguez@pichincha.gob.ec	cdominguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1551	20	\N	JULIO ANIBAL	PAZMIÑO RODRIGUEZ	\N	\N	\N	\N	\N	\N	jpazmino@pichincha.gob.ec	jpazmino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1552	20	\N	PAUL HANNIBAL	SEVILLA TINAJERO	\N	\N	\N	\N	\N	\N	psevilla@pichincha.gob.ec	psevilla	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1553	20	\N	MONICA ALEXANDRA	LEON ARROBA	\N	\N	\N	\N	\N	\N	leonm@pichincha.gob.ec	leonm	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1554	20	\N	HECTOR RAMIRO	CARRANCO ROMERO	\N	\N	\N	\N	\N	\N	rcarranco@pichincha.gob.ec	rcarranco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1555	20	\N	MARIA VICTORIA	PROAÑO CHIRIBOGA	\N	\N	\N	\N	\N	\N	vproano@pichincha.gob.ec	vproano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1556	20	\N	JULIA EDITH	ROBERT GARCES	\N	\N	\N	\N	\N	\N	jroberts@pichincha.gob.ec	jroberts	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1557	20	\N	MARGARITA DEL	CONSUELO ALVAREZ PABON	\N	\N	\N	\N	\N	\N	malvarez@pichincha.gob.ec	malvarez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1558	20	\N	JORGE BRIGIDO	CHAVEZ PILCO	\N	\N	\N	\N	\N	\N	jchavez@pichincha.gob.ec	jchavez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1559	20	\N	IVAN GONZALO	TREJO ORDOÑEZ	\N	\N	\N	\N	\N	\N	itrejo@pichincha.gob.ec	itrejo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1560	20	\N	ANGEL GUALBERTO	QUIROZ VALLEJO	\N	\N	\N	\N	\N	\N	aquiroz@pichincha.gob.ec	aquiroz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1561	20	\N	GONZALO PAUL	MACHADO ROSERO	\N	\N	\N	\N	\N	\N	gmachado@pichincha.gob.ec	gmachado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1562	20	\N	TATIANA FERNANDA	CRUZ CHACON	\N	\N	\N	\N	\N	\N	tcruz@pichincha.gob.ec	tcruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1563	20	\N	MARCELO DANIEL	MOSQUERA MEJIA	\N	\N	\N	\N	\N	\N	mmosquera@pichincha.gob.ec	mmosquera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1564	20	\N	CARLA MARCELA	ALTAMIRANO SANTACRUZ	\N	\N	\N	\N	\N	\N	caltamirano@pichincha.gob.ec	caltamirano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1565	20	\N	WILSON KLEVER	GUAÑA GALLEGOS	\N	\N	\N	\N	\N	\N	wguana@pichincha.gob.ec	wguana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1566	20	\N	HUGO MARCELO	VILLAGOMEZ ALVEAR	\N	\N	\N	\N	\N	\N	hvillagomez@pichincha.gob.ec	hvillagomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1567	20	\N	TEOFILO FABIAN	UZCATEGUI CALISTO	\N	\N	\N	\N	\N	\N	fuzcategui@pichincha.gob.ec	fuzcategui	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1568	20	\N	JAZMIN MARIBEL	MOYANO LUCIO	\N	\N	\N	\N	\N	\N	jmoyano@pichincha.gob.ec	jmoyano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1569	20	\N	ENMA YOLANDA	SANDOVAL CARLOSAMA	\N	\N	\N	\N	\N	\N	esandoval@pichincha.gob.ec	esandoval	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1570	20	\N	PATRICIA JANETH	KAROLYS GORDON	\N	\N	\N	\N	\N	\N	pkarolys@pichincha.gob.ec	pkarolys	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1571	20	\N	PABLO FERNANDO	PARRA ACOSTA	\N	\N	\N	\N	\N	\N	pparra@pichincha.gob.ec	pparra	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1572	20	\N	HERNAN ANDRES	MARTINEZ MARTINEZ	\N	\N	\N	\N	\N	\N	amartinez@pichincha.gob.ec	amartinez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1573	20	\N	ANGEL CARLOS	VILLARRUEL GARCIA	\N	\N	\N	\N	\N	\N	avillarruel@pichincha.gob.ec	avillarruel	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1574	20	\N	CESAR ALFONSO	SANCHEZ MEDINA	\N	\N	\N	\N	\N	\N	casanchez@pichincha.gob.ec	casanchez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1575	20	\N	EDISON XAVIER	FLORES OÑA	\N	\N	\N	\N	\N	\N	eflores@pichincha.gob.ec	eflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1576	20	\N	HUGO HUMBERTO	HIDALGO AYALA	\N	\N	\N	\N	\N	\N	hhidalgo@pichincha.gob.ec	hhidalgo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1577	20	\N	IRALDA CLAUDIA	ARMAS PILATASIG	\N	\N	\N	\N	\N	\N	iarmas@pichincha.gob.ec	iarmas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1578	20	\N	JEANNETH PAOLA	CIFUENTES MOLINA	\N	\N	\N	\N	\N	\N	jcifuentes@pichincha.gob.ec	jcifuentes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1579	20	\N	JHAMES MAGDALENA	BASSANTES ALARCON	\N	\N	\N	\N	\N	\N	jbassantes@pichincha.gob.ec	jbassantes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1580	20	\N	JOHANNA MARITZA	SATAMA JUMBO	\N	\N	\N	\N	\N	\N	jsatama@pichincha.gob.ec	jsatama	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1581	20	\N	DAVID ALEJANDRO	PAZMIÑO TERAN	\N	\N	\N	\N	\N	\N	dpazmino@pichincha.gob.ec	dpazmino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1582	20	\N	SILVANA GABRIELA	JIMENEZ ZAMBRANO	\N	\N	\N	\N	\N	\N	jimenezs@pichincha.gob.ec	jimenezs	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1583	20	\N	JOSE ALBERTO	HERRERA SIMBAÑA	\N	\N	\N	\N	\N	\N	aherrera@pichincha.gob.ec	aherrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1584	20	\N	JOHNNY ADALBERTO	ALVAREZ VASQUEZ	\N	\N	\N	\N	\N	\N	jalvarez@pichincha.gob.ec	jalvarez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1585	20	\N	JOSE ALCIDES	LOPEZ ROSERO	\N	\N	\N	\N	\N	\N	jlopez@pichincha.gob.ec	jlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1586	20	\N	MANUEL ENRIQUE	ESTUPIÑAN TACO	\N	\N	\N	\N	\N	\N	eestupinan@pichincha.gob.ec	eestupinan	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1587	20	\N	MARIO FABIAN	RUEDA MOYA	\N	\N	\N	\N	\N	\N	mrueda@pichincha.gob.ec	mrueda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1588	20	\N	MAYRA LUCIA	BOADA PUGA	\N	\N	\N	\N	\N	\N	mboada@pichincha.gob.ec	mboada	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1589	20	\N	PAULINA ALEXANDRA	GOMEZ GODOY	\N	\N	\N	\N	\N	\N	pgomez@pichincha.gob.ec	pgomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1590	20	\N	RICHARD GUSTAVO	FLORES OÑA	\N	\N	\N	\N	\N	\N	floresr@pichincha.gob.ec	floresr	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1591	20	\N	RUBEN ANDRES	PILLIZA RODRIGUEZ	\N	\N	\N	\N	\N	\N	apilliza@pichincha.gob.ec	apilliza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1592	20	\N	VICTOR HUGO	NARVAEZ PUERREZ	\N	\N	\N	\N	\N	\N	vnarvaez@pichincha.gob.ec	vnarvaez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1593	20	\N	GONZALO ANDRES	CAMPAÑA SANTACRUZ	\N	\N	\N	\N	\N	\N	gcampana@pichincha.gob.ec	gcampana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1594	20	\N	HUGO ALBERTO	PALLARES ESTRADA	\N	\N	\N	\N	\N	\N	hpallares@pichincha.gob.ec	hpallares	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1595	20	\N	JUAN CRISTOBAL	VALLEJO MEJIA	\N	\N	\N	\N	\N	\N	jvallejo@pichincha.gob.ec	jvallejo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1596	20	\N	LUIS ROBERTO	MOLINA SIMBAÑA	\N	\N	\N	\N	\N	\N	rmolina@pichincha.gob.ec	rmolina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1597	20	\N	MARIO PATRICIO	HIDALGO MARTINEZ	\N	\N	\N	\N	\N	\N	phidalgo@pichincha.gob.ec	phidalgo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1598	20	\N	NELSON RAFAEL	CRUZ FLORES	\N	\N	\N	\N	\N	\N	ncruz@pichincha.gob.ec	ncruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1599	20	\N	VICTOR HUGO	PONCE LOPEZ	\N	\N	\N	\N	\N	\N	hponce@pichincha.gob.ec	hponce	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1600	20	\N	VICTOR JUAN	VILLALBA ESPINOSA	\N	\N	\N	\N	\N	\N	jvillalba@pichincha.gob.ec	jvillalba	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1601	20	\N	ANAMARIA DEL	CISNE JIMENEZ CARVALLO	\N	\N	\N	\N	\N	\N	ajimenez@pichincha.gob.ec	ajimenez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1602	20	\N	DAVID ALEJANDRO	CASTRO ROMERO	\N	\N	\N	\N	\N	\N	dcastro@pichincha.gob.ec	dcastro	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1603	20	\N	MAURICIO DANIEL	CHECA BENAVIDES	\N	\N	\N	\N	\N	\N	dcheca@pichincha.gob.ec	dcheca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1604	20	\N	CINTHYA ARACELI	HERVAS NOVOA	\N	\N	\N	\N	\N	\N	chervas@pichincha.gob.ec	chervas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1605	20	\N	WILLIAM HENRY	LOPEZ PEREZ	\N	\N	\N	\N	\N	\N	wlopez@pichincha.gob.ec	wlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1606	20	\N	EDISON MAURICIO	GUERRON GARCIA	\N	\N	\N	\N	\N	\N	mguerron@pichincha.gob.ec	mguerron	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1607	20	\N	GABICHO ROMAN	HERRERA SALAZAR	\N	\N	\N	\N	\N	\N	herrerag@pichincha.gob.ec	herrerag	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1608	20	\N	VIRGILIO AUGUSTO	HERDOIZA ZABALA	\N	\N	\N	\N	\N	\N	vherdoiza@pichincha.gob.ec	vherdoiza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1609	20	\N	INES MARGARITA	CASERES FLORES	\N	\N	\N	\N	\N	\N	mcaseres@pichincha.gob.ec	mcaseres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1610	20	\N	MARIA ELENA	CAMACHO TRUJILLO	\N	\N	\N	\N	\N	\N	mcamacho@pichincha.gob.ec	mcamacho	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1611	20	\N	GLORIA DE	JESUS GONZALEZ ESTRELLA	\N	\N	\N	\N	\N	\N	ggonzalez@pichincha.gob.ec	ggonzalez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1612	20	\N	JOSE ORLANDO	CAZA SARABIA	\N	\N	\N	\N	\N	\N	jcaza@pichincha.gob.ec	jcaza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1613	20	\N	JOSE LUIS	PEREZ ALULEMA	\N	\N	\N	\N	\N	\N	jlperez@pichincha.gob.ec	jlperez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1614	20	\N	RUMIÑAHUI FRANKLIN	SIMBAÑA QUILUMBA	\N	\N	\N	\N	\N	\N	fsimbana@pichincha.gob.ec	fsimbana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1615	20	\N	STALIN SANTIAGO	ROJAS GOMEZ	\N	\N	\N	\N	\N	\N	srojas@pichincha.gob.ec	srojas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1616	20	\N	LUIS EDMUNDO	CACERES SILVA	\N	\N	\N	\N	\N	\N	lcaceres@pichincha.gob.ec	lcaceres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1617	20	\N	MARIELA NOEMI	VILLAMARIN PAREDES	\N	\N	\N	\N	\N	\N	mvillamarin@pichincha.gob.ec	mvillamarin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1618	20	\N	CRISTINA FABIOLA	GAVILANES ALBAN	\N	\N	\N	\N	\N	\N	cfgavilanes@pichincha.gob.ec	cfgavilanes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1619	20	\N	ALEXANDRA MARLENE	ESPIN TORRES	\N	\N	\N	\N	\N	\N	aespin@pichincha.gob.ec	aespin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1620	20	\N	MARIA ESTHER	ABALCO VASCONEZ	\N	\N	\N	\N	\N	\N	mabalco@pichincha.gob.ec	mabalco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1621	20	\N	FRANCISCO JAVIER	ARRIETA DEIDAN	\N	\N	\N	\N	\N	\N	farrieta@pichincha.gob.ec	farrieta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1622	20	\N	OMAR FAVIAN	CASTILLO CARRASCO	\N	\N	\N	\N	\N	\N	ocastillo@pichincha.gob.ec	ocastillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1623	20	\N	MARCO ANTONIO	CHAVEZ ARRIETA	\N	\N	\N	\N	\N	\N	marcochavez@pichincha.gob.ec	marcochavez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1624	20	\N	FRANKLIN GONZALO	DIAZ PEREZ	\N	\N	\N	\N	\N	\N	gdiaz@pichincha.gob.ec	gdiaz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1625	20	\N	MARIA MARGARITA	DUEÑAS MONTERO	\N	\N	\N	\N	\N	\N	mduenas@pichincha.gob.ec	mduenas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1626	20	\N	JOSE LUIS	GAVILANES ARIAS	\N	\N	\N	\N	\N	\N	jgavilanes@pichincha.gob.ec	jgavilanes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1627	20	\N	ROCIO DE	FATIMA GUERRERO ORBES	\N	\N	\N	\N	\N	\N	rguerrero@pichincha.gob.ec	rguerrero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1628	20	\N	SANDRA DEL	CARMEN LEON BENALCAZAR	\N	\N	\N	\N	\N	\N	sleon@pichincha.gob.ec	sleon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1629	20	\N	JORGE ALBERTO	MALDONADO MAINGON	\N	\N	\N	\N	\N	\N	jmaldonado@pichincha.gob.ec	jmaldonado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1630	20	\N	JACKELINE MARLENE	MARIÑO MEDINA	\N	\N	\N	\N	\N	\N	jmarino@pichincha.gob.ec	jmarino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1631	20	\N	JULIO ANIBAL	PERALVO AYALA	\N	\N	\N	\N	\N	\N	jperalvo@pichincha.gob.ec	jperalvo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1632	20	\N	MIRIAM RUTH	ROLDAN CRESPO	\N	\N	\N	\N	\N	\N	mroldan@pichincha.gob.ec	mroldan	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1633	20	\N	SONIA ELIZABETH	SAENZ TUFIÑO	\N	\N	\N	\N	\N	\N	ssaenz@pichincha.gob.ec	ssaenz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1634	20	\N	PATRICIO ALFONSO	SOLIS MARTINEZ	\N	\N	\N	\N	\N	\N	psolis@pichincha.gob.ec	psolis	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1635	20	\N	CECILIA DEL	PILAR SOSA VILLACRES	\N	\N	\N	\N	\N	\N	csosa@pichincha.gob.ec	csosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1636	20	\N	MIRIAM DEL	PILAR ULLOA POMBOZA	\N	\N	\N	\N	\N	\N	mulloa@pichincha.gob.ec	mulloa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1637	20	\N	LUIS EFRAIN	VILLACIS CRIOLLO	\N	\N	\N	\N	\N	\N	lvillacis@pichincha.gob.ec	lvillacis	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1638	20	\N	ADELA VICTORIA	JARRIN CHALONS	\N	\N	\N	\N	\N	\N	ajarrin@pichincha.gob.ec	ajarrin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1639	20	\N	JAVIER LUIS	BOLAGAY TIPANTA	\N	\N	\N	\N	\N	\N	jbolagay@pichincha.gob.ec	jbolagay	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1640	20	\N	GLADYS VIRGINIA	CURAY ULCUANGO	\N	\N	\N	\N	\N	\N	gcuray@pichincha.gob.ec	gcuray	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1641	20	\N	JORGE LUIS	ALBUJA MENA	\N	\N	\N	\N	\N	\N	jalbuja@pichincha.gob.ec	jalbuja	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1642	20	\N	JOSE ALFONSO	FLORES GUAMAN	\N	\N	\N	\N	\N	\N	jaflores@pichincha.gob.ec	jaflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1643	20	\N	LUIS EDUARDO	FLORES ROMAN	\N	\N	\N	\N	\N	\N	leflores@pichincha.gob.ec	leflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1644	20	\N	HOLGER ANTONIO	TOAPANTA CARVAJAL	\N	\N	\N	\N	\N	\N	htoapanta@pichincha.gob.ec	htoapanta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1645	20	\N	HELIRIA TOA	LAYEDRA CAMPANA	\N	\N	\N	\N	\N	\N	hlayedra@pichincha.gob.ec	hlayedra	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1646	20	\N	RAMIRO MARCELO	ZUMARRAGA AMORES	\N	\N	\N	\N	\N	\N	rzumarraga@pichincha.gob.ec	rzumarraga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1647	20	\N	ANGELES MARINA	PALMA PALMA	\N	\N	\N	\N	\N	\N	apalma@pichincha.gob.ec	apalma	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1648	20	\N	MARTHA MIREYA	VILLAVICENCIO MOREJON	\N	\N	\N	\N	\N	\N	mvillavicencio@pichincha.gob.ec	mvillavicencio	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1649	20	\N	MONICA PATRICIA	QUIROLA CADENA	\N	\N	\N	\N	\N	\N	mquirola@pichincha.gob.ec	mquirola	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1650	20	\N	MATILDE VERONICA	RENGIFO ZUMARRAGA	\N	\N	\N	\N	\N	\N	mrengifo@pichincha.gob.ec	mrengifo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1651	20	\N	JUAN CARLOS	SANTACRUZ SALINAS	\N	\N	\N	\N	\N	\N	jsantacruz@pichincha.gob.ec	jsantacruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1652	20	\N	MARCIA ZUSANA	SANTILLAN YANEZ	\N	\N	\N	\N	\N	\N	zsantillan@pichincha.gob.ec	zsantillan	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1653	20	\N	ANTONIO NAPOLEON	GUAMAN MUÑOZ	\N	\N	\N	\N	\N	\N	napoguaman@pichincha.gob.ec	napoguaman	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1654	20	\N	GUSTAVO PERICLES	TITUAÑA QUIJIA	\N	\N	\N	\N	\N	\N	gtituana@pichincha.gob.ec	gtituana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1655	20	\N	FRANKLIN TEODORO	RIVERA CARRERA	\N	\N	\N	\N	\N	\N	riveraf@pichincha.gob.ec	riveraf	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1656	20	\N	GUIDO FERNANDO	CAJAS LARA	\N	\N	\N	\N	\N	\N	fcajas@pichincha.gob.ec	fcajas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1657	20	\N	PATRICIO ELMER	PILAPAÑA ANDRANGO	\N	\N	\N	\N	\N	\N	ppilapana@pichincha.gob.ec	ppilapana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1658	20	\N	JIMMY PATRICIO	ALMEIDA CATOTA	\N	\N	\N	\N	\N	\N	jalmeida@pichincha.gob.ec	jalmeida	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1659	20	\N	JUAN AURELIO	GALARZA RON	\N	\N	\N	\N	\N	\N	jgalarza@pichincha.gob.ec	jgalarza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1660	20	\N	CARLOS RAMON	CALVACHI LANDETA	\N	\N	\N	\N	\N	\N	ccalvachi@pichincha.gob.ec	ccalvachi	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1661	20	\N	EDWIN SANTIAGO	GUERRERO NOBOA	\N	\N	\N	\N	\N	\N	esguerrero@pichincha.gob.ec	esguerrero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1662	20	\N	FERNANDO PATRICIO	TUZA CUZCO	\N	\N	\N	\N	\N	\N	ftuza@pichincha.gob.ec	ftuza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1663	20	\N	VICTOR HUGO	VILLACRESES PADILLA	\N	\N	\N	\N	\N	\N	vvillacreses@pichincha.gob.ec	vvillacreses	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1664	20	\N	EDISON WASHINGTON	GARCIA SALTOS	\N	\N	\N	\N	\N	\N	egarcia@pichincha.gob.ec	egarcia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1665	20	\N	BOLIVAR GUILLERMO	PORTILLA VELASQUEZ	\N	\N	\N	\N	\N	\N	bportilla@pichincha.gob.ec	bportilla	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1666	20	\N	JOSE LUIS	YUNGA SANCHEZ	\N	\N	\N	\N	\N	\N	jyunga@pichincha.gob.ec	jyunga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1667	20	\N	JUAN CARLOS	ORTEGA SARMIENTO	\N	\N	\N	\N	\N	\N	jortega@pichincha.gob.ec	jortega	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1668	20	\N	RODRIGO MARCELO	PAREDES AYORA	\N	\N	\N	\N	\N	\N	paredesr@pichincha.gob.ec	paredesr	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1669	20	\N	NEYBA KARENT	JARAMILLO BAHAMONDE	\N	\N	\N	\N	\N	\N	njaramillo@pichincha.gob.ec	njaramillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1670	20	\N	LIVIA MIROSLAVIA	VIVANCO VIVANCO	\N	\N	\N	\N	\N	\N	lvivanco@pichincha.gob.ec	lvivanco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1671	20	\N	GABRIEL RAMIRO	TAIPE CHAUCA	\N	\N	\N	\N	\N	\N	gtaipe@pichincha.gob.ec	gtaipe	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1672	20	\N	JOSE LUIS	ZAPATA MUÑOZ	\N	\N	\N	\N	\N	\N	jzapata@pichincha.gob.ec	jzapata	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1673	20	\N	FABIAN PATRICIO	VINUEZA UBIDIA	\N	\N	\N	\N	\N	\N	fvinueza@pichincha.gob.ec	fvinueza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1674	20	\N	MARIA HORTENCIA	URBINA POMBOSA	\N	\N	\N	\N	\N	\N	murbina@pichincha.gob.ec	murbina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1675	20	\N	SORAYDA MARIBEL	ROMAN LOPEZ	\N	\N	\N	\N	\N	\N	sroman@pichincha.gob.ec	sroman	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1676	20	\N	MARTHA YOLANDA	SANCHEZ LEON	\N	\N	\N	\N	\N	\N	msanchez@pichincha.gob.ec	msanchez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1677	20	\N	GLADYS PATRICIA	LOZA SAMBRANO	\N	\N	\N	\N	\N	\N	ploza@pichincha.gob.ec	ploza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1678	20	\N	GLADYS MARISOL	ROMAN GALARZA	\N	\N	\N	\N	\N	\N	mroman@pichincha.gob.ec	mroman	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1679	20	\N	CARLOS MARCELO	CRUZ SANDOVAL	\N	\N	\N	\N	\N	\N	cmcruz@pichincha.gob.ec	cmcruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1680	20	\N	JORGE PATRICIO	TOAPANTA TORRES	\N	\N	\N	\N	\N	\N	ptoapanta@pichincha.gob.ec	ptoapanta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1681	20	\N	LUIS EDUARDO	TOSCANO CHECA	\N	\N	\N	\N	\N	\N	etoscano@pichincha.gob.ec	etoscano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1682	20	\N	GABRIELA CORNELIA	CORNEJO REDROBAN	\N	\N	\N	\N	\N	\N	gcornejo@pichincha.gob.ec	gcornejo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1683	20	\N	LILIA MAGDALENA	ACOSTA ROMERO	\N	\N	\N	\N	\N	\N	lacosta@pichincha.gob.ec	lacosta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1684	20	\N	MARIA EUGENIA	MARTINEZ RAMIREZ	\N	\N	\N	\N	\N	\N	memartinez@pichincha.gob.ec	memartinez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1685	20	\N	JUAN BOSCO	TORRES CHICA	\N	\N	\N	\N	\N	\N	jbtorres@pichincha.gob.ec	jbtorres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1686	20	\N	TELMO ALFREDO	MALDONADO PEREZ	\N	\N	\N	\N	\N	\N	tmaldonado@pichincha.gob.ec	tmaldonado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1687	20	\N	JOSE VICENTE	CARDENAS MORA	\N	\N	\N	\N	\N	\N	cardenasj@pichincha.gob.ec	cardenasj	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1688	20	\N	JORGE SILVER	VERA SANCHEZ	\N	\N	\N	\N	\N	\N	jvera@pichincha.gob.ec	jvera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1689	20	\N	MANUEL FRANCISCO	ARGUELLO CORONEL	\N	\N	\N	\N	\N	\N	farguello@pichincha.gob.ec	farguello	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1690	20	\N	BOLIVAR EDISON	PORTILLA RAMIREZ	\N	\N	\N	\N	\N	\N	eportilla@pichincha.gob.ec	eportilla	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1691	20	\N	MARIO FERNANDO	CARDENAS HIDALGO	\N	\N	\N	\N	\N	\N	mcardenas@pichincha.gob.ec	mcardenas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1692	20	\N	ANDREA VALERIA	CALDERON MONTALVO	\N	\N	\N	\N	\N	\N	vcalderon@pichincha.gob.ec	vcalderon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1693	20	\N	PABLO SALOMON	TAPIA CASTILLO	\N	\N	\N	\N	\N	\N	ptapia@pichincha.gob.ec	ptapia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1694	20	\N	RODRIGO MARCO	SANCHEZ NEIRA	\N	\N	\N	\N	\N	\N	sanchezm@pichincha.gob.ec	sanchezm	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1695	20	\N	JAIME ALFONSO	VINUEZA ARMAS	\N	\N	\N	\N	\N	\N	jvinueza@pichincha.gob.ec	jvinueza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1696	20	\N	WILSON BAYARDO	MANOSALVAS ENRIQUEZ	\N	\N	\N	\N	\N	\N	wmanosalvas@pichincha.gob.ec	wmanosalvas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1697	20	\N	RICHARD ECUADOR	MARTINEZ ROCA	\N	\N	\N	\N	\N	\N	rmartinezr@pichincha.gob.ec	rmartinezr	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1698	20	\N	FELIPE AMARILDO	SUAREZ JARAMILLO	\N	\N	\N	\N	\N	\N	fsuarez@pichincha.gob.ec	fsuarez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1699	20	\N	ROSA MATILDE	FONSECA VILLARREAL	\N	\N	\N	\N	\N	\N	rfonseca@pichincha.gob.ec	rfonseca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1700	20	\N	NELSON BOLIVAR	GUAMAN OLMEDO	\N	\N	\N	\N	\N	\N	nguaman@pichincha.gob.ec	nguaman	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1701	20	\N	EDWIN RODRIGO	HERRERA VILLARREAL	\N	\N	\N	\N	\N	\N	eherrera@pichincha.gob.ec	eherrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1702	20	\N	MIGUEL ANGEL	VELASTEGUI ESCOBAR	\N	\N	\N	\N	\N	\N	mvelastegui@pichincha.gob.ec	mvelastegui	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1703	20	\N	FRANCISCO RODRIGO	ACHIG SUBIA	\N	\N	\N	\N	\N	\N	fachig@pichincha.gob.ec	fachig	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1704	20	\N	LUIS PATRICIO	AGUILAR AGUIRRE	\N	\N	\N	\N	\N	\N	paguilar@pichincha.gob.ec	paguilar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1705	20	\N	MARCELO ALEXANDER	AGUIRRE CEVALLOS	\N	\N	\N	\N	\N	\N	marceloaguirre@pichincha.gob.ec	marceloaguirre	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1706	20	\N	PAULO FERNANDO	ALVAREZ LOPEZ	\N	\N	\N	\N	\N	\N	palvarez@pichincha.gob.ec	palvarez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1707	20	\N	HUGO EDUARDO	BOLAÑOS ORBE	\N	\N	\N	\N	\N	\N	hbolanos@pichincha.gob.ec	hbolanos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1708	20	\N	MARTHA ESPERANZA	CAZAR ALMEIDA	\N	\N	\N	\N	\N	\N	mcazar@pichincha.gob.ec	mcazar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1709	20	\N	JESUS CAYETANO	FLORES QUINCHIGUANGO	\N	\N	\N	\N	\N	\N	jcflores@pichincha.gob.ec	jcflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1710	20	\N	CESAR VICENTE	GARZON ZAMBRANO	\N	\N	\N	\N	\N	\N	garzonc@pichincha.gob.ec	garzonc	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1711	20	\N	ELIZABETH NATALY	GORDILLO MARTINEZ	\N	\N	\N	\N	\N	\N	egordillo@pichincha.gob.ec	egordillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1712	20	\N	BOLIVAR GABRIEL	PARRA SAAVEDRA	\N	\N	\N	\N	\N	\N	bparra@pichincha.gob.ec	bparra	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1713	20	\N	LUIS EDUARDO	GUAMANI OÑA	\N	\N	\N	\N	\N	\N	lguamani@pichincha.gob.ec	lguamani	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1714	20	\N	MARITZA LEONOR	JAMA MOREIRA	\N	\N	\N	\N	\N	\N	mjama@pichincha.gob.ec	mjama	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1715	20	\N	BLANCA SOFIA	MAILA MAILA	\N	\N	\N	\N	\N	\N	bmaila@pichincha.gob.ec	bmaila	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1716	20	\N	LINLEY DAYSELLER	MENA VILLACIS	\N	\N	\N	\N	\N	\N	lmena@pichincha.gob.ec	lmena	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1717	20	\N	CHRISTIAN MAURICIO	MONRROY ROCANO	\N	\N	\N	\N	\N	\N	cmonrroy@pichincha.gob.ec	cmonrroy	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1718	20	\N	JULIO CESAR	MORA QUINTEROS	\N	\N	\N	\N	\N	\N	jmora@pichincha.gob.ec	jmora	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1719	20	\N	RENE VINICIO	MUÑOZ ARROBA	\N	\N	\N	\N	\N	\N	rmunoz@pichincha.gob.ec	rmunoz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1720	20	\N	FAUSTO FERNANDO	MURILLO ARROBA	\N	\N	\N	\N	\N	\N	fmurillo@pichincha.gob.ec	fmurillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1721	20	\N	RAUL ENRIQUE	OLMEDO JACOME	\N	\N	\N	\N	\N	\N	rolmedo@pichincha.gob.ec	rolmedo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1722	20	\N	HUGO MARCELO	ORBEA PEÑAFIEL	\N	\N	\N	\N	\N	\N	horbea@pichincha.gob.ec	horbea	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1723	20	\N	EDGAR GIOVANNI	VENEGAS MARTINEZ	\N	\N	\N	\N	\N	\N	edvenegas@pichincha.gob.ec	edvenegas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1724	20	\N	LUIS GUSTAVO	MUÑOZ MORETA	\N	\N	\N	\N	\N	\N	gmunoz@pichincha.gob.ec	gmunoz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1725	20	\N	ANTONIO PATRICIO	DIAZ MALDONADO	\N	\N	\N	\N	\N	\N	pdiaz@pichincha.gob.ec	pdiaz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1726	20	\N	MARIA EUGENIA	ROCA VICHAY	\N	\N	\N	\N	\N	\N	mroca@pichincha.gob.ec	mroca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1727	20	\N	CARLOS ESTUARDO	ATAHUALPA MEJIA	\N	\N	\N	\N	\N	\N	catahualpa@pichincha.gob.ec	catahualpa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1728	20	\N	JOSE AUGUSTO	CARDENAS VILELA	\N	\N	\N	\N	\N	\N	jcardenas@pichincha.gob.ec	jcardenas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1729	20	\N	OSWALDO AMABLE	ARIAS ARIAS	\N	\N	\N	\N	\N	\N	oarias@pichincha.gob.ec	oarias	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1730	20	\N	MARLENE ANGELITA	GARZON VILLEGAS	\N	\N	\N	\N	\N	\N	mgarzonv@pichincha.gob.ec	mgarzonv	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1731	20	\N	ANA PATRICIA	RUEDA CISNEROS	\N	\N	\N	\N	\N	\N	prueda@pichincha.gob.ec	prueda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1732	20	\N	GUILLERMO EDUARDO	LUNA RIOFRIO	\N	\N	\N	\N	\N	\N	lunag@pichincha.gob.ec	lunag	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1733	20	\N	WILLIAM ROBERTO	POZO TAPIA	\N	\N	\N	\N	\N	\N	rpozo@pichincha.gob.ec	rpozo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1734	20	\N	DARWIN OMAR	TROYA AGUILAR	\N	\N	\N	\N	\N	\N	dtroya@pichincha.gob.ec	dtroya	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1735	20	\N	SOL MARIA	JIMENEZ SILVA	\N	\N	\N	\N	\N	\N	sjimenez@pichincha.gob.ec	sjimenez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1736	20	\N	FABIAN EDUARDO	CAJAS CUEVA	\N	\N	\N	\N	\N	\N	cajasf@pichincha.gob.ec	cajasf	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1737	20	\N	ANGELITA GUADALUPE	GARZON PROAÑO	\N	\N	\N	\N	\N	\N	agarzon@pichincha.gob.ec	agarzon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1738	20	\N	EDWIN ABRAHAM	TORRES GUAICHA	\N	\N	\N	\N	\N	\N	eatorres@pichincha.gob.ec	eatorres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1739	20	\N	CARMEN EDELINA	GAVILANES JIMENEZ	\N	\N	\N	\N	\N	\N	cgavilanes@pichincha.gob.ec	cgavilanes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1740	20	\N	XIMENA MAXIMINA	FLORES VEGA	\N	\N	\N	\N	\N	\N	xflores@pichincha.gob.ec	xflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1741	20	\N	MARCO PATRICIO	LOPEZ ARSINIEGA	\N	\N	\N	\N	\N	\N	plopez@pichincha.gob.ec	plopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1742	20	\N	FANNY LEONOR	VILLA RUIZ	\N	\N	\N	\N	\N	\N	fvilla@pichincha.gob.ec	fvilla	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1743	20	\N	MARIA DEL	CARMEN TAMAYO CEVALLOS	\N	\N	\N	\N	\N	\N	mctamayo@pichincha.gob.ec	mctamayo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1744	20	\N	ANGEL BOLIVAR	REINA NARANJO	\N	\N	\N	\N	\N	\N	breina@pichincha.gob.ec	breina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1745	20	\N	AIDA MERCEDES	GUALOTUÑA CHINCHILEMA	\N	\N	\N	\N	\N	\N	mgualotuna@pichincha.gob.ec	mgualotuna	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1746	20	\N	ALBA LILIAN	MIÑO DIAZ	\N	\N	\N	\N	\N	\N	minoa@pichincha.gob.ec	minoa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1747	20	\N	JAVIER ANIBAL	HIDALGO SALGUERO	\N	\N	\N	\N	\N	\N	jhidalgo@pichincha.gob.ec	jhidalgo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1748	20	\N	EDWIN RODRIGO	MIÑO ARCOS	\N	\N	\N	\N	\N	\N	emino@pichincha.gob.ec	emino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1749	20	\N	ELISABETH DEL	ROCIO AREVALO BOHORQUEZ	\N	\N	\N	\N	\N	\N	earevalo@pichincha.gob.ec	earevalo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1750	20	\N	MONICA DEL	ROCIO HIDALGO PINTO	\N	\N	\N	\N	\N	\N	mhidalgo@pichincha.gob.ec	mhidalgo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1751	20	\N	MARIA JOSE	ALMACHE CADENA	\N	\N	\N	\N	\N	\N	malmache@pichincha.gob.ec	malmache	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1752	20	\N	MILTON FERNANDO	FUELANTALA AVILA	\N	\N	\N	\N	\N	\N	mfuelantala@pichincha.gob.ec	mfuelantala	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1753	20	\N	ANITA LUCIA	TORRES VACA	\N	\N	\N	\N	\N	\N	torresa@pichincha.gob.ec	torresa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1754	20	\N	MARIA AMPARO	NICOLALDE TUPIZA	\N	\N	\N	\N	\N	\N	anicolalde@pichincha.gob.ec	anicolalde	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1755	20	\N	EVELYN ISABEL	GUERRERO ACOSTA	\N	\N	\N	\N	\N	\N	eguerrero@pichincha.gob.ec	eguerrero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1756	20	\N	DAYSI MARIBEL	MORALES RIVADENEIRA	\N	\N	\N	\N	\N	\N	dmorales@pichincha.gob.ec	dmorales	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1757	20	\N	ROBERT EUGENIO	CARPIO MENDIETA	\N	\N	\N	\N	\N	\N	rcarpio@pichincha.gob.ec	rcarpio	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1758	20	\N	SONIA JANNETH	HALLO LEIVA	\N	\N	\N	\N	\N	\N	shallo@pichincha.gob.ec	shallo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1759	20	\N	HECTOR ANDRES	AGUIRRE VILLARREAL	\N	\N	\N	\N	\N	\N	aaguirre@pichincha.gob.ec	aaguirre	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1760	20	\N	VICTOR HUGO	HERNANDEZ JACOME	\N	\N	\N	\N	\N	\N	vhernandez@pichincha.gob.ec	vhernandez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1761	20	\N	SERGIO HERNAN	SEVILLA CEVALLOS	\N	\N	\N	\N	\N	\N	ssevillac@pichincha.gob.ec	ssevillac	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1762	20	\N	DEISY MARILU	ZAMBRANO MACIAS	\N	\N	\N	\N	\N	\N	dmzambrano@pichincha.gob.ec	dmzambrano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1763	20	\N	BOLIVAR ARTEMIO	MOLINA PAZMIÑO	\N	\N	\N	\N	\N	\N	bmolina@pichincha.gob.ec	bmolina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1764	20	\N	RAUL FERNANDO	SUAREZ ORTIZ	\N	\N	\N	\N	\N	\N	rsuarez@pichincha.gob.ec	rsuarez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1765	20	\N	LUIS EDUARDO	MALDONADO CAMINO	\N	\N	\N	\N	\N	\N	emaldonado@pichincha.gob.ec	emaldonado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1766	20	\N	ANDREA LILIANA	PEREIRA PALADINES	\N	\N	\N	\N	\N	\N	lpereira@pichincha.gob.ec	lpereira	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1767	20	\N	EDGAR PATRICIO	OLMEDO BENALCAZAR	\N	\N	\N	\N	\N	\N	polmedo@pichincha.gob.ec	polmedo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1768	20	\N	EDISON ISAIAS	GOMEZ HIDALGO	\N	\N	\N	\N	\N	\N	egomez@pichincha.gob.ec	egomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1769	20	\N	CARLOS FERNANDO	PURCACHI ALMEIDA	\N	\N	\N	\N	\N	\N	fpurcachi@pichincha.gob.ec	fpurcachi	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1770	20	\N	DIEGO VINICIO	GALARZA DUQUE	\N	\N	\N	\N	\N	\N	dgalarza@pichincha.gob.ec	dgalarza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1771	20	\N	JUAN MIGUEL	HINOJOSA LARCO	\N	\N	\N	\N	\N	\N	jhinojosa@pichincha.gob.ec	jhinojosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1772	20	\N	LUIS GERMAN	LAHUATTE ROJAS	\N	\N	\N	\N	\N	\N	glahuate@pichincha.gob.ec	glahuate	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1773	20	\N	RUTH IRALDA	JIMENEZ CISNEROS	\N	\N	\N	\N	\N	\N	ijimenez@pichincha.gob.ec	ijimenez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1774	20	\N	CARLOS HUMBERTO	CAJAMARCA CORREA	\N	\N	\N	\N	\N	\N	ccajamarca@pichincha.gob.ec	ccajamarca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1775	20	\N	PAOLA KRUPSKAYA	ARANDA SALINAS	\N	\N	\N	\N	\N	\N	paranda@pichincha.gob.ec	paranda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1776	20	\N	RUTH MARLENE	CORDOVA AGUILAR	\N	\N	\N	\N	\N	\N	rcordova@pichincha.gob.ec	rcordova	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1777	20	\N	LUIS HAMILTON	TRUJILLO VILLAVICENSIO	\N	\N	\N	\N	\N	\N	htrujillo@pichincha.gob.ec	htrujillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1778	20	\N	MARIA ISABEL	BEJARANO YEPEZ	\N	\N	\N	\N	\N	\N	ibejarano@pichincha.gob.ec	ibejarano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1779	20	\N	JUAN ROBERT	LASCANO MIÑO	\N	\N	\N	\N	\N	\N	jlascano@pichincha.gob.ec	jlascano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1780	20	\N	HUGO ESTUARDO	NAVARRETE NAVARRETE	\N	\N	\N	\N	\N	\N	hnavarrete@pichincha.gob.ec	hnavarrete	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1781	20	\N	MIGUEL ANGEL	PATIÑO OROZCO	\N	\N	\N	\N	\N	\N	mpatino@pichincha.gob.ec	mpatino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1782	20	\N	GRACIELA PATRICIA	CALDERON GALLEGOS	\N	\N	\N	\N	\N	\N	pcalderon@pichincha.gob.ec	pcalderon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1783	20	\N	KYCO ALDEMAR	REAL ALQUINGA	\N	\N	\N	\N	\N	\N	kreal@pichincha.gob.ec	kreal	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1784	20	\N	XIMENA ALEXANDRA	MEDINA TREJO	\N	\N	\N	\N	\N	\N	xmedina@pichincha.gob.ec	xmedina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1785	20	\N	FRANCISCO JAVIER	GUERRERO MELO	\N	\N	\N	\N	\N	\N	jguerrero@pichincha.gob.ec	jguerrero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1786	20	\N	FERNANDO PATRICIO	CELI GUERRERO	\N	\N	\N	\N	\N	\N	fceli@pichincha.gob.ec	fceli	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1787	20	\N	GONZALO ANTONIL	REYES AGUIRRE	\N	\N	\N	\N	\N	\N	greyes@pichincha.gob.ec	greyes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1788	20	\N	SUSANA ISABEL	TAFUR JARAMILLO	\N	\N	\N	\N	\N	\N	stafur@pichincha.gob.ec	stafur	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1789	20	\N	IRENE DEL	ROCIO JIMENEZ SILVA	\N	\N	\N	\N	\N	\N	rjimenez@pichincha.gob.ec	rjimenez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1790	20	\N	JENNIFER MONSERRATH	SALVADOR PALLASCO	\N	\N	\N	\N	\N	\N	jsalvador@pichincha.gob.ec	jsalvador	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1791	20	\N	LUIS ALSONSO	MORENO SALAZAR	\N	\N	\N	\N	\N	\N	amoreno@pichincha.gob.ec	amoreno	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1792	20	\N	JOANA ELIZABETH	CALDERON TAYUPANTA	\N	\N	\N	\N	\N	\N	jcalderon@pichincha.gob.ec	jcalderon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1793	20	\N	ELENA DEL	ROSARIO VALENCIA ALVEAR	\N	\N	\N	\N	\N	\N	evalencia@pichincha.gob.ec	evalencia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1794	20	\N	IVAN FERNADO	GALLARDO RIPALDA	\N	\N	\N	\N	\N	\N	igallardo@pichincha.gob.ec	igallardo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1795	20	\N	MIGUEL EDUARDO	SOSA ESPINOSA	\N	\N	\N	\N	\N	\N	msosa@pichincha.gob.ec	msosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1796	20	\N	ALEX PAUL	MOROCHO SIMBAÑA	\N	\N	\N	\N	\N	\N	amorocho@pichincha.gob.ec	amorocho	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1797	20	\N	BURBANO AYALA	PABLO EFRAIN	\N	\N	\N	\N	\N	\N	peburbano@pichincha.gob.ec	peburbano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1798	20	\N	DINO ALBERTO	ALBAN COBOS	\N	\N	\N	\N	\N	\N	dalban@pichincha.gob.ec	dalban	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1799	20	\N	DARWIN ROBERTO	JIJON LARCO	\N	\N	\N	\N	\N	\N	djijon@pichincha.gob.ec	djijon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1800	20	\N	WILFRIDO RENE	ESPIN LAMAR	\N	\N	\N	\N	\N	\N	respin@pichincha.gob.ec	respin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1801	20	\N	EDISON DARIO	MOLINA TOAPANTA	\N	\N	\N	\N	\N	\N	emolina@pichincha.gob.ec	emolina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1802	20	\N	LUIS GUILLERMO	TACO ESTRELLA	\N	\N	\N	\N	\N	\N	gtaco@pichincha.gob.ec	gtaco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1803	20	\N	FIEL DIVINA	CHAMPANG CEDEÑO	\N	\N	\N	\N	\N	\N	dchampang@pichincha.gob.ec	dchampang	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1804	20	\N	PABLO ENRIQUE	MOLINA VILLALBA	\N	\N	\N	\N	\N	\N	pmolina@pichincha.gob.ec	pmolina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1805	20	\N	DALIS GERMANIA	FIERRO BOLAÑOS	\N	\N	\N	\N	\N	\N	dfierro@pichincha.gob.ec	dfierro	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1806	20	\N	GLADYS TERESA	ROMERO MENA	\N	\N	\N	\N	\N	\N	tromero@pichincha.gob.ec	tromero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1807	20	\N	XIMENA DEL	ROCIO VILLAMARIN GARZON	\N	\N	\N	\N	\N	\N	xvillamarin@pichincha.gob.ec	xvillamarin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1808	20	\N	AMADA ELVIDA	MIÑO VARGAS	\N	\N	\N	\N	\N	\N	amino@pichincha.gob.ec	amino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1809	20	\N	ANGEL FABIAN	QUISHPE CARRERA	\N	\N	\N	\N	\N	\N	fquishpe@pichincha.gob.ec	fquishpe	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1810	20	\N	CARLOS ALBERTO	TOAPANTA GAVILANEZ	\N	\N	\N	\N	\N	\N	ctoapanta@pichincha.gob.ec	ctoapanta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1811	20	\N	KATALINA EUGENIA	PAZ TORRES	\N	\N	\N	\N	\N	\N	kpaz@pichincha.gob.ec	kpaz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1812	20	\N	LUIS FERNANDO	VILLAMARIN CIFUENTES	\N	\N	\N	\N	\N	\N	fvillamarin@pichincha.gob.ec	fvillamarin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1813	20	\N	VICENTE OMAR	ARAUJO DURAN	\N	\N	\N	\N	\N	\N	oaraujo@pichincha.gob.ec	oaraujo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1814	20	\N	NANCY DEL	PILAR GARZON FRAGA	\N	\N	\N	\N	\N	\N	ngarzon@pichincha.gob.ec	ngarzon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1815	20	\N	CARLOS GUSTAVO	VACAS MOLINA	\N	\N	\N	\N	\N	\N	gvacas@pichincha.gob.ec	gvacas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1816	20	\N	SILVIA JAQUELINE	MULLO ROJAS	\N	\N	\N	\N	\N	\N	smullo@pichincha.gob.ec	smullo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1817	20	\N	DAVID FERNANDO	PUMA ROMERO	\N	\N	\N	\N	\N	\N	dpuma@pichincha.gob.ec	dpuma	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1818	20	\N	MARIA CRISTINA	GUERRERO VELAZQUEZ	\N	\N	\N	\N	\N	\N	mguerrero@pichincha.gob.ec	mguerrero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1819	20	\N	JOHAN ARMANDO	COYAGO MONTENEGRO	\N	\N	\N	\N	\N	\N	jcoyago@pichincha.gob.ec	jcoyago	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1820	20	\N	SINSO MIGUEL	FLORES MESA	\N	\N	\N	\N	\N	\N	mflores@pichincha.gob.ec	mflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1821	20	\N	JOSE LUIS	ESCUDERO VALLEJO	\N	\N	\N	\N	\N	\N	jescudero@pichincha.gob.ec	jescudero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1822	20	\N	ALEXANDRA JUDITH	ANDINO HERRERA	\N	\N	\N	\N	\N	\N	aandino@pichincha.gob.ec	aandino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1823	20	\N	ANABEL DEL	ROCIO PEREZ SOLA	\N	\N	\N	\N	\N	\N	aperez@pichincha.gob.ec	aperez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1824	20	\N	ANDRES SANTIAGO	PEÑAHERRERA NAVAS	\N	\N	\N	\N	\N	\N	spenaherrera@pichincha.gob.ec	spenaherrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1825	20	\N	EDISON FRANCISCO	MARURI MILLER	\N	\N	\N	\N	\N	\N	emaruri@pichincha.gob.ec	emaruri	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1826	20	\N	GABRIEL JUAN	ORTIZ LEON	\N	\N	\N	\N	\N	\N	jortiz@pichincha.gob.ec	jortiz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1827	20	\N	ALBERTO GERARDO	GARCIA SALAMEA	\N	\N	\N	\N	\N	\N	aggarcia@pichincha.gob.ec	aggarcia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1828	20	\N	GUADALUPE DEL	ROCIO QUISPE ANALUISA	\N	\N	\N	\N	\N	\N	gquispe@pichincha.gob.ec	gquispe	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1829	20	\N	ROCIO DEL	PILAR FLORES LEON	\N	\N	\N	\N	\N	\N	rfloresl@pichincha.gob.ec	rfloresl	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1830	20	\N	JENNY LUCIA	DAZA PORTERO	\N	\N	\N	\N	\N	\N	jdaza@pichincha.gob.ec	jdaza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1831	20	\N	LEONARDO ELISEO	PAZ COSTA	\N	\N	\N	\N	\N	\N	lpaz@pichincha.gob.ec	lpaz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1832	20	\N	JAIME ROGER	GUILLEN JIMENEZ	\N	\N	\N	\N	\N	\N	jguillen@pichincha.gob.ec	jguillen	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1833	20	\N	MARIA VICTORIA	AVELLANEDA DIAZ	\N	\N	\N	\N	\N	\N	mavellaneda@pichincha.gob.ec	mavellaneda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1834	20	\N	ALBERTO ISRAEL	MONTENEGRO ROLDAN	\N	\N	\N	\N	\N	\N	amontenegro@pichincha.gob.ec	amontenegro	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1835	20	\N	RAMON RAFAEL	GARCIA SANCHEZ	\N	\N	\N	\N	\N	\N	rgarcia@pichincha.gob.ec	rgarcia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1836	20	\N	VIRGINIA ALICIA	TAPIA SOLORZANO	\N	\N	\N	\N	\N	\N	vtapia@pichincha.gob.ec	vtapia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1837	20	\N	JORGE WASHINGTON	MARTINEZ VERDESOTO	\N	\N	\N	\N	\N	\N	jmartinez@pichincha.gob.ec	jmartinez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1838	20	\N	JENNIFER LORENA	RUIZ GALARZA	\N	\N	\N	\N	\N	\N	jruiz@pichincha.gob.ec	jruiz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1839	20	\N	PAVEL AHEMED	MINDA BOTERO	\N	\N	\N	\N	\N	\N	pminda@pichincha.gob.ec	pminda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1840	20	\N	FANNY DE	JESUS CABRERA ZAMBRANO	\N	\N	\N	\N	\N	\N	ecabrera@pichincha.gob.ec	ecabrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1841	20	\N	MIGUEL ANGEL	REASCOS FERNANDEZ	\N	\N	\N	\N	\N	\N	mreascos@pichincha.gob.ec	mreascos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1842	20	\N	MARCO ANTONIO	BARROS VISCARRA	\N	\N	\N	\N	\N	\N	mbarros@pichincha.gob.ec	mbarros	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1843	20	\N	LUIS EDUARDO	RODRIGUEZ RODRIGUEZ	\N	\N	\N	\N	\N	\N	edurodriguez@pichincha.gob.ec	edurodriguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1844	20	\N	FRANCISCO MARCELO	YACELGA TERAN	\N	\N	\N	\N	\N	\N	fyacelga@pichincha.gob.ec	fyacelga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1845	20	\N	FRANCISCO MARCELO	YACELGA TERAN 1	\N	\N	\N	\N	\N	\N	fyacelga1@pichincha.gob.ec	fyacelga1	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1846	20	\N	SONIA VIVIANA	LASSO LOPEZ	\N	\N	\N	\N	\N	\N	slasso@pichincha.gob.ec	slasso	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1847	20	\N	CECILIA GUADALUPE	OLMEDO SUAREZ	\N	\N	\N	\N	\N	\N	colmedo@pichincha.gob.ec	colmedo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1848	20	\N	SHIRLEY ADELINA	ROMERO ACOSTA	\N	\N	\N	\N	\N	\N	sromero@pichincha.gob.ec	sromero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1849	20	\N	ALEXANDRA KARINA	CANDO ALVISA	\N	\N	\N	\N	\N	\N	candok@pichincha.gob.ec	candok	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1850	20	\N	ISABEL CRISTINA	ARCOS HURTADO	\N	\N	\N	\N	\N	\N	iarcos@pichincha.gob.ec	iarcos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1851	20	\N	CLARA TATIANA	MOREIRA CALVACHE	\N	\N	\N	\N	\N	\N	tmoreira@pichincha.gob.ec	tmoreira	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1852	20	\N	DAYSI KARINA	SANTIANA CUALCHI	\N	\N	\N	\N	\N	\N	ksantiana@pichincha.gob.ec	ksantiana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1853	20	\N	GEOCONDA JOHANNA	AGUILAR MALDONADO	\N	\N	\N	\N	\N	\N	jhaguilar@pichincha.gob.ec	jhaguilar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1854	20	\N	GEORGE VICENTE	LLANES SUAREZ	\N	\N	\N	\N	\N	\N	gllanes@pichincha.gob.ec	gllanes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1855	20	\N	ENMA JACQUELINE	CARRERA CHAVEZ	\N	\N	\N	\N	\N	\N	jcarrera@pichincha.gob.ec	jcarrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1856	20	\N	LUIS FERNANDO	OJEDA AYALA	\N	\N	\N	\N	\N	\N	lojeda@pichincha.gob.ec	lojeda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1857	20	\N	GUIDO LUDGARDO	NEPTALI ROMERO LARCO	\N	\N	\N	\N	\N	\N	romerog@pichincha.gob.ec	romerog	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1858	20	\N	ROSA IRMA	AGUILAR ARZA	\N	\N	\N	\N	\N	\N	raguilar@pichincha.gob.ec	raguilar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1859	20	\N	HUGO FERNANDO	CALVOPIÑA MONTENEGRO	\N	\N	\N	\N	\N	\N	fcalvopina@pichincha.gob.ec	fcalvopina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1860	20	\N	ROBERTO CRISTOBAL	ZAPATA COFRE	\N	\N	\N	\N	\N	\N	rzapata@pichincha.gob.ec	rzapata	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1861	20	\N	JESUS ALONSO	BASANTES VACACELA	\N	\N	\N	\N	\N	\N	abasantes@pichincha.gob.ec	abasantes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1862	20	\N	HERNAN VICENTE	LARA CUESTA	\N	\N	\N	\N	\N	\N	hlara@pichincha.gob.ec	hlara	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1863	20	\N	CRISTIAN FERNANDO	VIVERO ERAZO	\N	\N	\N	\N	\N	\N	cvivero@pichincha.gob.ec	cvivero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1864	20	\N	LUIS ALBERTO	MAYA AGUIRRE	\N	\N	\N	\N	\N	\N	lmaya@pichincha.gob.ec	lmaya	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1865	20	\N	ANGEL CRISTIA	CHASIPANTA TIPANGUANO	\N	\N	\N	\N	\N	\N	achasipanta@pichincha.gob.ec	achasipanta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1866	20	\N	CARLOS ALBERTO	DIAZ OCAÑA	\N	\N	\N	\N	\N	\N	cdiaz@pichincha.gob.ec	cdiaz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1867	20	\N	RUBEN DARIO	SANTACRUZ VINUEZA	\N	\N	\N	\N	\N	\N	rsantacruz@pichincha.gob.ec	rsantacruz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1868	20	\N	ANGEL EDUARDO	ARMIJO LOGROÑO	\N	\N	\N	\N	\N	\N	aarmijo@pichincha.gob.ec	aarmijo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1869	20	\N	VICENTE MARCELO	RAMIREZ MORENO	\N	\N	\N	\N	\N	\N	mramirez@pichincha.gob.ec	mramirez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1870	20	\N	PEDRO VINICIO	CADENA VALENZUELA	\N	\N	\N	\N	\N	\N	pcadena@pichincha.gob.ec	pcadena	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1871	20	\N	WILSON GUSTAVO	ALMEIDA ANGO	\N	\N	\N	\N	\N	\N	walmeida@pichincha.gob.ec	walmeida	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1872	20	\N	JUAN MANUEL	TIRARDO VASCONEZ	\N	\N	\N	\N	\N	\N	jtirado@pichincha.gob.ec	jtirado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1873	20	\N	JORGE LUIS	SALGUERO PAZOS	\N	\N	\N	\N	\N	\N	jsalguero@pichincha.gob.ec	jsalguero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1874	20	\N	LUIS RAMIRO	DAVILA GRANDA	\N	\N	\N	\N	\N	\N	ldavila@pichincha.gob.ec	ldavila	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1875	20	\N	ARTURO ISAAC	VILLAFUERTE CHATA	\N	\N	\N	\N	\N	\N	avillafuerte@pichincha.gob.ec	avillafuerte	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1876	20	\N	MIGUEL AURELIO	SALTOS DELGADO	\N	\N	\N	\N	\N	\N	msaltos@pichincha.gob.ec	msaltos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1877	20	\N	WILSON RENE	ZURITA RODRIGUEZ	\N	\N	\N	\N	\N	\N	wzurita@pichincha.gob.ec	wzurita	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1878	20	\N	INGRID VILMA	LOPEZ CEDEÑO	\N	\N	\N	\N	\N	\N	vlopez@pichincha.gob.ec	vlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1879	20	\N	MANUEL ARTURO	GARCIA GARCIA	\N	\N	\N	\N	\N	\N	agarcia@pichincha.gob.ec	agarcia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1880	20	\N	GONZALO EDUARDO	NAVARRETE PAREDES	\N	\N	\N	\N	\N	\N	enavarrete@pichincha.gob.ec	enavarrete	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1881	20	\N	WALTER ROGER	NAZATE MARMOL	\N	\N	\N	\N	\N	\N	wnazate@pichincha.gob.ec	wnazate	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1882	20	\N	LUIS ANIBAL	YANEZ CALDERON	\N	\N	\N	\N	\N	\N	ayanez@pichincha.gob.ec	ayanez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1883	20	\N	EZEQUIEL FLORENTINO	GUAJALA PINTA	\N	\N	\N	\N	\N	\N	eguajala@pichincha.gob.ec	eguajala	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1884	20	\N	VICTOR MANUEL	AGUILAR SANTACRUZ	\N	\N	\N	\N	\N	\N	vaguilar@pichincha.gob.ec	vaguilar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1885	20	\N	CELSO TEOFILO	TITUAÑA CINALI	\N	\N	\N	\N	\N	\N	ctituana@pichincha.gob.ec	ctituana	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1886	20	\N	MARIA FERNANDA	CUSTODE ALTAMIRANO	\N	\N	\N	\N	\N	\N	mcustode@pichincha.gob.ec	mcustode	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1887	20	\N	MARCO VINICIO	ESCUDERO CARRANCO	\N	\N	\N	\N	\N	\N	mescudero@pichincha.gob.ec	mescudero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1888	20	\N	SORAYA CECILIA	HIDALGO OJEDA	\N	\N	\N	\N	\N	\N	shidalgo@pichincha.gob.ec	shidalgo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1889	20	\N	ANDRES SEBASTIAN	GALARZA MARTINEZ	\N	\N	\N	\N	\N	\N	agalarza@pichincha.gob.ec	agalarza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1890	20	\N	IVAN ENRIQUE	VILLAGOMEZ BUENO	\N	\N	\N	\N	\N	\N	ivillagomez@pichincha.gob.ec	ivillagomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1891	20	\N	TELMO ALONSO	CUADRADO CASTELO	\N	\N	\N	\N	\N	\N	acuadrado@pichincha.gob.ec	acuadrado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1892	20	\N	MARCELO ALONSO	CARRASCO COBO	\N	\N	\N	\N	\N	\N	mcarrasco@pichincha.gob.ec	mcarrasco	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1893	20	\N	JAIME ESTUARDO	REVELO MONTEGRO	\N	\N	\N	\N	\N	\N	jrevelo@pichincha.gob.ec	jrevelo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1894	20	\N	LUIS EDUARDO	VIVAS GALARZA	\N	\N	\N	\N	\N	\N	evivas@pichincha.gob.ec	evivas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1895	20	\N	MARCO FABIAN	ORTIZ CADENA	\N	\N	\N	\N	\N	\N	mortiz@pichincha.gob.ec	mortiz	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1896	20	\N	LUIS WASHINGTON	LUNA TORRES	\N	\N	\N	\N	\N	\N	wluna@pichincha.gob.ec	wluna	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1897	20	\N	EDGAR EUGENIO	RIVADENEIRA TELLO	\N	\N	\N	\N	\N	\N	erivadeneira@pichincha.gob.ec	erivadeneira	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1898	20	\N	BLANCA MARIA	RIVADENEIRA VINUEZA	\N	\N	\N	\N	\N	\N	brivadeneira@pichincha.gob.ec	brivadeneira	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1899	20	\N	OTILIA ROCIO	MAFLA ROMERO	\N	\N	\N	\N	\N	\N	rmafla@pichincha.gob.ec	rmafla	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1900	20	\N	MELINA GIOVANNA	AGUIRRE VASQUEZ	\N	\N	\N	\N	\N	\N	maguirre@pichincha.gob.ec	maguirre	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1901	20	\N	HERNAN PATRICIO	CORONEL RUIZ	\N	\N	\N	\N	\N	\N	hcoronel@pichincha.gob.ec	hcoronel	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1902	20	\N	LUIS ERNESTO	ECHEVERRIA TAFUR	\N	\N	\N	\N	\N	\N	lecheverria@pichincha.gob.ec	lecheverria	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1903	20	\N	MERCEDES MARGARITA	ZURITA LARA	\N	\N	\N	\N	\N	\N	mzurita@pichincha.gob.ec	mzurita	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1904	20	\N	LIDIA GRACIELA	ESPIN PAZMIÑO	\N	\N	\N	\N	\N	\N	lgespin@pichincha.gob.ec	lgespin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1905	20	\N	MARTHA CECILIA	MORALES CHAQUINGA	\N	\N	\N	\N	\N	\N	mmorales@pichincha.gob.ec	mmorales	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1906	20	\N	MARIA ISABEL	ROMERO PROAÑO	\N	\N	\N	\N	\N	\N	miromero@pichincha.gob.ec	miromero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1907	20	\N	GERARDO PATRICIO	CARRION LEON	\N	\N	\N	\N	\N	\N	carrionp@pichincha.gob.ec	carrionp	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1908	20	\N	MARCELO IVAN	VACA SAA	\N	\N	\N	\N	\N	\N	mvaca@pichincha.gob.ec	mvaca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1909	20	\N	MARCO ANTONIO	CELI MALDONADO	\N	\N	\N	\N	\N	\N	mceli@pichincha.gob.ec	mceli	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1910	20	\N	WASHINGTON NAPOLEON	HEREDIA AGUIRRE	\N	\N	\N	\N	\N	\N	wheredia@pichincha.gob.ec	wheredia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1911	20	\N	LIGIA ROCIO	CAIZA VALENCIA	\N	\N	\N	\N	\N	\N	lcaiza@pichincha.gob.ec	lcaiza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1912	20	\N	GABRIEL BENJAMIN	AGUILAR RIVADENEIRA	\N	\N	\N	\N	\N	\N	gaguilar@pichincha.gob.ec	gaguilar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1913	20	\N	MARIO PATRICIO	BECERRA VALDIVIEZO	\N	\N	\N	\N	\N	\N	pbecerra@pichincha.gob.ec	pbecerra	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1914	20	\N	SOFIA CAROLINA	BENITEZ PROAÑO	\N	\N	\N	\N	\N	\N	cbenitez@pichincha.gob.ec	cbenitez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1915	20	\N	GONZALO JOSE	BUSTOS DAVILA	\N	\N	\N	\N	\N	\N	gbustos@pichincha.gob.ec	gbustos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1916	20	\N	MARIA LORENA	GONZALEZ ANASCO	\N	\N	\N	\N	\N	\N	lgonzalez@pichincha.gob.ec	lgonzalez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1917	20	\N	OSCAR EDUARDO	LUNA QUELAL	\N	\N	\N	\N	\N	\N	oluna@pichincha.gob.ec	oluna	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1918	20	\N	MARIA LISSET	MEDINA PROAÑO	\N	\N	\N	\N	\N	\N	lmedina@pichincha.gob.ec	lmedina	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1919	20	\N	DARWIN JAVIER	MEJIA FONSECA	\N	\N	\N	\N	\N	\N	dmejia@pichincha.gob.ec	dmejia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1920	20	\N	EDWIN ABRAHAM	PAEZ GUAMAN	\N	\N	\N	\N	\N	\N	epaez@pichincha.gob.ec	epaez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1921	20	\N	INES AIDE	PANELUISA GUANO	\N	\N	\N	\N	\N	\N	ipaneluisa@pichincha.gob.ec	ipaneluisa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1922	20	\N	KATHYA BELEN	PINOS LEON	\N	\N	\N	\N	\N	\N	kpinos@pichincha.gob.ec	kpinos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1923	20	\N	CESAR VICENTE	ROVALINO BRAVO	\N	\N	\N	\N	\N	\N	crovalino@pichincha.gob.ec	crovalino	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1924	20	\N	ALVA GARDENIA	RODRIGUEZ FLORES	\N	\N	\N	\N	\N	\N	arodriguez@pichincha.gob.ec	arodriguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1925	20	\N	CARLA DANIELA	TAPIA ARAUJO	\N	\N	\N	\N	\N	\N	ctapia@pichincha.gob.ec	ctapia	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1926	20	\N	MARCO FERNANDO	ARMAS BENEGAS	\N	\N	\N	\N	\N	\N	marmas@pichincha.gob.ec	marmas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1927	20	\N	MILTON ALEXEI	TORRES JUM	\N	\N	\N	\N	\N	\N	mtorres@pichincha.gob.ec	mtorres	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1928	20	\N	ELSA BEATRIZ	VANEGAS MORA	\N	\N	\N	\N	\N	\N	evanegas@pichincha.gob.ec	evanegas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1929	20	\N	MARIA TERESA	LOPEZ ALMEIDA	\N	\N	\N	\N	\N	\N	tlopez@pichincha.gob.ec	tlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1930	20	\N	ABDON FRANCISCO	GOMEZ HIDALGO	\N	\N	\N	\N	\N	\N	agomez@pichincha.gob.ec	agomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1931	20	\N	IVAN PATRICIO	CHANGO LUGMAÑA	\N	\N	\N	\N	\N	\N	ichango@pichincha.gob.ec	ichango	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1932	20	\N	DIANA ELIZABETH	CABRERA TAMAYO	\N	\N	\N	\N	\N	\N	dcabrera@pichincha.gob.ec	dcabrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1933	20	\N	REINALDO VINICIO	YANEZ PALIZ	\N	\N	\N	\N	\N	\N	ryanez@pichincha.gob.ec	ryanez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1934	20	\N	DAVID FERNANDO	JARRIN CERDA	\N	\N	\N	\N	\N	\N	djarrin@pichincha.gob.ec	djarrin	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1935	20	\N	HENRY MANUEL	ARTEAGA MALDONADO	\N	\N	\N	\N	\N	\N	harteaga@pichincha.gob.ec	harteaga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1936	20	\N	JOSE LIZANDRO	ARMIJO ZURITA	\N	\N	\N	\N	\N	\N	jarmijo@pichincha.gob.ec	jarmijo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1937	20	\N	MANUEL TELESFORO	CORREA SATAMA	\N	\N	\N	\N	\N	\N	mcorrea@pichincha.gob.ec	mcorrea	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1938	20	\N	CARLOS HERMEL	FLORES VIDAL	\N	\N	\N	\N	\N	\N	cflores@pichincha.gob.ec	cflores	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1939	20	\N	MARCIA EDY	GAVILANEZ XIMENEZ	\N	\N	\N	\N	\N	\N	mgavilanez@pichincha.gob.ec	mgavilanez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1940	20	\N	FREDDY ISRAEL	NARVAEZ PULLOPAXI	\N	\N	\N	\N	\N	\N	finarvaez@pichincha.gob.ec	finarvaez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1941	20	\N	JANNETHE DEL	PILAR PEREZ ESPINOSA	\N	\N	\N	\N	\N	\N	pperez@pichincha.gob.ec	pperez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1942	20	\N	SYLVIA XIMENA	TRUJILLO ACOSTA	\N	\N	\N	\N	\N	\N	xtrujillo@pichincha.gob.ec	xtrujillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1943	20	\N	MONICA MAGDALENA	ALVARADO VEINTIMILLA	\N	\N	\N	\N	\N	\N	malvarado@pichincha.gob.ec	malvarado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1944	20	\N	Administrador GSTI	GSTI	\N	\N	\N	\N	\N	\N	AdminGsti@pichincha.gob.ec	AdminGsti	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1946	20	\N	PATRICIA RODRIGUEZ	RODRIGUEZ	\N	\N	\N	\N	\N	\N	prodriguez@pichincha.gob.ec	prodriguez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1947	20	\N	BETTY ILLESCAS	LEON	\N	\N	\N	\N	\N	\N	billescas@pichincha.gob.ec	billescas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1948	20	\N	FRITZ DIEPRICH	REINTHALLER	\N	\N	\N	\N	\N	\N	freinthaller@pichincha.gob.ec	freinthaller	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1949	20	\N	HIPOLITO REYEZ	RAMIREZ	\N	\N	\N	\N	\N	\N	hreyes@pichincha.gob.ec	hreyes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1950	20	\N	BENJAMIN EZEQUIEL	EZEQUIEL	\N	\N	\N	\N	\N	\N	bdejesus@pichincha.gob.ec	bdejesus	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1951	20	\N	MARIO GUILLERMO	URCUANGO	\N	\N	\N	\N	\N	\N	murcuango@pichincha.gob.ec	murcuango	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1952	20	\N	GIOVANNA DE	DE	\N	\N	\N	\N	\N	\N	ggordillo@pichincha.gob.ec	ggordillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1953	20	\N	ARAM MAZAHERI	MAZAHERI	\N	\N	\N	\N	\N	\N	amazaheri@pichincha.gob.ec	amazaheri	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1954	20	\N	RAFAEL BAYARDO	GALARZA	\N	\N	\N	\N	\N	\N	bgalarza@pichincha.gob.ec	bgalarza	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1955	20	\N	RODRIGO SALTOS	FALQUEZ	\N	\N	\N	\N	\N	\N	rsaltos@pichincha.gob.ec	rsaltos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1956	20	\N	MARIA DE	DE	\N	\N	\N	\N	\N	\N	mmerizalde@pichincha.gob.ec	mmerizalde	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1957	20	\N	JAIME RAUL	RAUL	\N	\N	\N	\N	\N	\N	jdelatorre@pichincha.gob.ec	jdelatorre	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1958	20	\N	PIEDAD DE	DE	\N	\N	\N	\N	\N	\N	pgalarraga@pichincha.gob.ec	pgalarraga	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1959	20	\N	ANDRES WLADIMIR	LUCERO	\N	\N	\N	\N	\N	\N	wlucero@pichincha.gob.ec	wlucero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1960	20	\N	GLADYS JUDITH	BEDON	\N	\N	\N	\N	\N	\N	gbedon@pichincha.gob.ec	gbedon	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1961	20	\N	MIGUEL ANGEL	PILATAXI	\N	\N	\N	\N	\N	\N	mpilataxi@pichincha.gob.ec	mpilataxi	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1962	20	\N	ALFREDO MALDONADO	MORALES	\N	\N	\N	\N	\N	\N	samaldonado@pichincha.gob.ec	samaldonado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1963	20	\N	JUAN PEÑAHERRERA	CAMPAÑA	\N	\N	\N	\N	\N	\N	jpenaherrera@pichincha.gob.ec	jpenaherrera	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1964	20	\N	STALIN UTRERAS	VARGAS	\N	\N	\N	\N	\N	\N	sutreras@pichincha.gob.ec	sutreras	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1965	20	\N	MARGARITA FLOR	FLOR	\N	\N	\N	\N	\N	\N	mcasierra@pichincha.gob.ec	mcasierra	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1966	20	\N	LLUVIA ESTRELLA	ESTRELLA	\N	\N	\N	\N	\N	\N	llvelez@pichincha.gob.ec	llvelez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1967	20	\N	LAURA TIRADO	TIRADO	\N	\N	\N	\N	\N	\N	ltirado@pichincha.gob.ec	ltirado	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1968	20	\N	MARCO CAJAS	VILATUÑA	\N	\N	\N	\N	\N	\N	mcajas@pichincha.gob.ec	mcajas	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1969	20	\N	GEOVANNY GOYES	MONTES	\N	\N	\N	\N	\N	\N	ggoyes@pichincha.gob.ec	ggoyes	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1970	20	\N	PABLO PEREZ	SANCHEZ	\N	\N	\N	\N	\N	\N	pperezs@pichincha.gob.ec	pperezs	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1971	20	\N	STEPHANIE AVALOS	AVALOS	\N	\N	\N	\N	\N	\N	savalos@pichincha.gob.ec	savalos	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1972	20	\N	JORGE ALBERTO	CARPIO	\N	\N	\N	\N	\N	\N	jcarpio@pichincha.gob.ec	jcarpio	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1973	20	\N	GERARDO VIRACUCHA	CALDERON	\N	\N	\N	\N	\N	\N	gviracucha@pichincha.gob.ec	gviracucha	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1974	20	\N	RINA TENESACA	TENEDA	\N	\N	\N	\N	\N	\N	ritenesaca@pichincha.gob.ec	ritenesaca	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1975	20	\N	GEOVANNY ESPINOSA	TROYA	\N	\N	\N	\N	\N	\N	gespinosa@pichincha.gob.ec	gespinosa	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1976	20	\N	LUIS FERNANDO	GOMEZ	\N	\N	\N	\N	\N	\N	lgomez@pichincha.gob.ec	lgomez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1977	20	\N	PLINIO VINICIO	CHIMBORAZO	\N	\N	\N	\N	\N	\N	vchimborazo@pichincha.gob.ec	vchimborazo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1978	20	\N	ESTEFANIA SOLANO	SOLANO	\N	\N	\N	\N	\N	\N	esolano@pichincha.gob.ec	esolano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1979	20	\N	BETTY XIMENA	MONCAYO	\N	\N	\N	\N	\N	\N	xmoncayo@pichincha.gob.ec	xmoncayo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1980	20	\N	MERCEDEZ NORMA	LOPEZ	\N	\N	\N	\N	\N	\N	mlopez@pichincha.gob.ec	mlopez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1981	20	\N	SUSANA DE	DE	\N	\N	\N	\N	\N	\N	serazo@pichincha.gob.ec	serazo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1982	20	\N	PAUL ROMERO	MONTENEGRO	\N	\N	\N	\N	\N	\N	promero@pichincha.gob.ec	promero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1983	20	\N	AQUILINO IZURIETA	BASTIDAS	\N	\N	\N	\N	\N	\N	aizurieta@pichincha.gob.ec	aizurieta	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1984	20	\N	RUTH SALGUERO	TOLGUILLA	\N	\N	\N	\N	\N	\N	rsalguero@pichincha.gob.ec	rsalguero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1985	20	\N	FATIMA ARIAS	CALVACHE	\N	\N	\N	\N	\N	\N	farias@pichincha.gob.ec	farias	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1986	20	\N	TATIANA BENALCAZAR	VILLACRECES	\N	\N	\N	\N	\N	\N	tbenalcazar@pichincha.gob.ec	tbenalcazar	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1987	20	\N	ALFONSO CASTILLO	VACA	\N	\N	\N	\N	\N	\N	acastillo@pichincha.gob.ec	acastillo	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1988	20	\N	PATRICIO CHANGO	IVAN	\N	\N	\N	\N	\N	\N	pchango@pichincha.gob.ec	pchango	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1989	20	\N	ANDREA LOYOLA	GARCIA	\N	\N	\N	\N	\N	\N	aloyola@pichincha.gob.ec	aloyola	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1990	20	\N	DIEGO MARQUEZ	ALMEIDA	\N	\N	\N	\N	\N	\N	dmarquez@pichincha.gob.ec	dmarquez	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1991	20	\N	ARMANDO ROMERO	SOTOMAYOR	\N	\N	\N	\N	\N	\N	aromeros@pichincha.gob.ec	aromeros	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1992	20	\N	JUDITH SERRANO	VILLALBA	\N	\N	\N	\N	\N	\N	jserrano@pichincha.gob.ec	jserrano	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1993	20	\N	ELIECER TAFUR	YEPEZ	\N	\N	\N	\N	\N	\N	etafur@pichincha.gob.ec	etafur	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1994	20	\N	ALEXANDRA MASABANDA	MASABANDA	\N	\N	\N	\N	\N	\N	amasabanda@pichincha.gob.ec	amasabanda	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1995	20	\N	RAMIRO ROMERO	MUÑOZ	\N	\N	\N	\N	\N	\N	rromero@pichincha.gob.ec	rromero	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1996	20	\N	ESTEBAN LOPEZ	GRANIZO	\N	\N	\N	\N	\N	\N	elopezg@pichincha.gob.ec	elopezg	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1997	20	\N	PAUL TERAN	TERAN	\N	\N	\N	\N	\N	\N	pteran@pichincha.gob.ec	pteran	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
1998	20	\N	MACARENA CARRERA	CARRERA	\N	\N	\N	\N	\N	\N	carreram@pichincha.gob.ec	carreram	202cb962ac59075b964b07152d234b70	0	\N	\N	\N	0	\N	\N	\N
\.


--
-- TOC entry 2338 (class 0 OID 0)
-- Dependencies: 196
-- Name: prsn_prsn__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('prsn_prsn__id_seq', 1998, true);


--
-- TOC entry 2237 (class 0 OID 104071)
-- Dependencies: 197
-- Data for Name: prtr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY prtr (prtr__id, rltr__id, prsn__id, trmt__id, prtrobsr, prtrprms, prtrfcar, prtrfcen, prtrfclr, prtrfcrc, prtrfcrs, dpto__id) FROM stdin;
\.


--
-- TOC entry 2339 (class 0 OID 0)
-- Dependencies: 198
-- Name: prtr_prtr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('prtr_prtr__id_seq', 99, true);


--
-- TOC entry 2239 (class 0 OID 104079)
-- Dependencies: 199
-- Data for Name: prus; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY prus (prus__id, prsn__id, perm__id, prusfcin, prusfcfn, prusobsv, prsnasgn) FROM stdin;
1027	63	13	2014-02-06	\N	\N	63
1028	63	3	2014-03-03	2014-03-13	\N	63
1029	63	3	2014-03-12	\N	\N	63
1030	63	11	2014-03-10	\N	\N	63
1031	63	16	2014-03-10	\N	\N	63
351	\N	13	2014-01-14	2014-01-21	\N	63
352	\N	13	2014-01-13	2014-01-22	\N	63
\.


--
-- TOC entry 2340 (class 0 OID 0)
-- Dependencies: 200
-- Name: prus_prus__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('prus_prus__id_seq', 1035, true);


--
-- TOC entry 2241 (class 0 OID 104084)
-- Dependencies: 201
-- Data for Name: rltr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY rltr (rltr__id, rltrcdgo, rltrdscr) FROM stdin;
1	R001	PARA
2	R002	COPIA
3	E003	RECIBE
4	E004	ENVIA
5	I005	IMPRIME
\.


--
-- TOC entry 2341 (class 0 OID 0)
-- Dependencies: 202
-- Name: rltr_rltr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('rltr_rltr__id_seq', 4, true);


--
-- TOC entry 2243 (class 0 OID 104089)
-- Dependencies: 203
-- Data for Name: sesn; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY sesn (sesn__id, prfl__id, prsn__id) FROM stdin;
7	1	63
15	1	63
45	3	1522
46	3	1945
47	3	63
48	3	1464
49	3	1465
50	3	1466
51	3	1467
52	3	1468
53	3	1469
54	3	1470
55	3	1471
56	3	1472
57	3	1473
58	3	1474
59	3	1475
60	3	1476
61	3	1477
62	3	1478
63	3	1479
64	3	1480
65	3	1481
66	3	1482
67	3	1483
68	3	1484
69	3	1485
70	3	1486
71	3	1487
72	3	1488
73	3	1489
74	3	1490
75	3	1491
76	3	1492
77	3	1493
78	3	1494
79	3	1495
80	3	1496
81	3	1497
82	3	1498
83	3	1499
84	3	1500
85	3	1501
86	3	1502
87	3	1503
88	3	1504
89	3	1505
90	3	1506
91	3	1507
92	3	1508
93	3	1509
94	3	1510
95	3	1511
96	3	1512
97	3	1513
98	3	1514
99	3	1515
100	3	1516
101	3	1517
102	3	1518
103	3	1519
104	3	1520
105	3	1521
106	3	1523
107	3	1524
108	3	1525
109	3	1526
110	3	1527
111	3	1528
112	3	1529
113	3	1530
114	3	1531
115	3	1532
116	3	1533
117	3	1534
118	3	1535
119	3	1536
120	3	1537
121	3	1538
122	3	1539
123	3	1540
124	3	1541
125	3	1542
126	3	1543
127	3	1544
128	3	1545
129	3	1546
130	3	1547
131	3	1548
132	3	1549
133	3	1550
134	3	1551
135	3	1552
136	3	1553
137	3	1554
138	3	1555
139	3	1556
140	3	1557
141	3	1558
142	3	1559
143	3	1560
144	3	1561
145	3	1562
146	3	1563
147	3	1564
148	3	1565
149	3	1566
150	3	1567
151	3	1568
152	3	1569
153	3	1570
154	3	1571
155	3	1572
156	3	1573
157	3	1574
158	3	1575
159	3	1576
160	3	1577
161	3	1578
162	3	1579
163	3	1580
164	3	1581
165	3	1582
166	3	1583
167	3	1584
168	3	1585
169	3	1586
170	3	1587
171	3	1588
172	3	1589
173	3	1590
174	3	1591
175	3	1592
176	3	1593
177	3	1594
178	3	1595
179	3	1596
180	3	1597
181	3	1598
182	3	1599
183	3	1600
184	3	1601
185	3	1602
186	3	1603
187	3	1604
188	3	1605
189	3	1606
190	3	1607
191	3	1608
192	3	1609
193	3	1610
194	3	1611
195	3	1612
196	3	1613
197	3	1614
198	3	1615
199	3	1616
200	3	1617
201	3	1618
202	3	1619
203	3	1620
204	3	1621
205	3	1622
206	3	1623
207	3	1624
208	3	1625
209	3	1626
210	3	1627
211	3	1628
212	3	1629
213	3	1630
214	3	1631
215	3	1632
216	3	1633
217	3	1634
218	3	1635
219	3	1636
220	3	1637
221	3	1638
222	3	1639
223	3	1640
224	3	1641
225	3	1642
226	3	1643
227	3	1644
228	3	1645
229	3	1646
230	3	1647
231	3	1648
232	3	1649
233	3	1650
234	3	1651
235	3	1652
236	3	1653
237	3	1654
238	3	1655
239	3	1656
240	3	1657
241	3	1658
242	3	1659
243	3	1660
244	3	1661
245	3	1662
246	3	1663
247	3	1664
248	3	1665
249	3	1666
250	3	1667
251	3	1668
252	3	1669
253	3	1670
254	3	1671
255	3	1672
256	3	1673
257	3	1674
258	3	1675
259	3	1676
260	3	1677
261	3	1678
262	3	1679
263	3	1680
264	3	1681
265	3	1682
266	3	1683
267	3	1684
268	3	1685
269	3	1686
270	3	1687
271	3	1688
272	3	1689
273	3	1690
274	3	1691
275	3	1692
276	3	1693
277	3	1694
278	3	1695
279	3	1696
280	3	1697
281	3	1698
282	3	1699
283	3	1700
284	3	1701
285	3	1702
286	3	1703
287	3	1704
288	3	1705
289	3	1706
290	3	1707
291	3	1708
292	3	1709
293	3	1710
294	3	1711
295	3	1712
296	3	1713
297	3	1714
298	3	1715
299	3	1716
300	3	1717
301	3	1718
302	3	1719
303	3	1720
304	3	1721
305	3	1722
306	3	1723
307	3	1724
308	3	1725
309	3	1726
310	3	1727
311	3	1728
312	3	1729
313	3	1730
314	3	1731
315	3	1732
316	3	1733
317	3	1734
318	3	1735
319	3	1736
320	3	1737
321	3	1738
322	3	1739
323	3	1740
324	3	1741
325	3	1742
326	3	1743
327	3	1744
328	3	1745
329	3	1746
330	3	1747
331	3	1748
332	3	1749
333	3	1750
334	3	1751
335	3	1752
336	3	1753
337	3	1754
338	3	1755
339	3	1756
340	3	1757
341	3	1758
342	3	1759
343	3	1760
344	3	1761
345	3	1762
346	3	1763
347	3	1764
348	3	1765
349	3	1766
350	3	1767
351	3	1768
352	3	1769
353	3	1770
354	3	1771
355	3	1772
356	3	1773
357	3	1774
358	3	1775
359	3	1776
360	3	1777
361	3	1778
362	3	1779
363	3	1780
364	3	1781
365	3	1782
366	3	1783
367	3	1784
368	3	1785
369	3	1786
370	3	1787
371	3	1788
372	3	1789
373	3	1790
374	3	1791
375	3	1792
376	3	1793
377	3	1794
378	3	1795
379	3	1796
380	3	1797
381	3	1798
382	3	1799
383	3	1800
384	3	1801
385	3	1802
386	3	1803
387	3	1804
388	3	1805
389	3	1806
390	3	1807
391	3	1808
392	3	1809
393	3	1810
394	3	1811
395	3	1812
396	3	1813
397	3	1814
398	3	1815
399	3	1816
400	3	1817
401	3	1818
402	3	1819
403	3	1820
404	3	1821
405	3	1822
406	3	1823
407	3	1824
408	3	1825
409	3	1826
410	3	1827
411	3	1828
412	3	1829
413	3	1830
414	3	1831
415	3	1832
416	3	1833
417	3	1834
418	3	1835
419	3	1836
420	3	1837
421	3	1838
422	3	1839
423	3	1840
424	3	1841
425	3	1842
426	3	1843
427	3	1844
428	3	1845
429	3	1846
430	3	1847
431	3	1848
432	3	1849
433	3	1850
434	3	1851
435	3	1852
436	3	1853
437	3	1854
438	3	1855
439	3	1856
440	3	1857
441	3	1858
442	3	1859
443	3	1860
444	3	1861
445	3	1862
446	3	1863
447	3	1864
448	3	1865
449	3	1866
450	3	1867
451	3	1868
452	3	1869
453	3	1870
454	3	1871
455	3	1872
456	3	1873
457	3	1874
458	3	1875
459	3	1876
460	3	1877
461	3	1878
462	3	1879
463	3	1880
464	3	1881
465	3	1882
466	3	1883
467	3	1884
468	3	1885
469	3	1886
470	3	1887
471	3	1888
472	3	1889
473	3	1890
474	3	1891
475	3	1892
476	3	1893
477	3	1894
478	3	1895
479	3	1896
480	3	1897
481	3	1898
482	3	1899
483	3	1900
484	3	1901
485	3	1902
486	3	1903
487	3	1904
488	3	1905
489	3	1906
490	3	1907
491	3	1908
492	3	1909
493	3	1910
494	3	1911
495	3	1912
496	3	1913
497	3	1914
498	3	1915
499	3	1916
500	3	1917
501	3	1918
502	3	1919
503	3	1920
504	3	1921
505	3	1922
506	3	1923
507	3	1924
508	3	1925
509	3	1926
510	3	1927
511	3	1928
512	3	1929
513	3	1930
514	3	1931
515	3	1932
516	3	1933
517	3	1934
518	3	1935
519	3	1936
520	3	1937
521	3	1938
522	3	1939
523	3	1940
524	3	1941
525	3	1942
526	3	1943
527	3	1944
528	3	1946
529	3	1947
530	3	1948
531	3	1949
532	3	1950
533	3	1951
534	3	1952
535	3	1953
536	3	1954
537	3	1955
538	3	1956
539	3	1957
540	3	1958
541	3	1959
542	3	1960
543	3	1961
544	3	1962
545	3	1963
546	3	1964
547	3	1965
548	3	1966
549	3	1967
550	3	1968
551	3	1969
552	3	1970
553	3	1971
554	3	1972
555	3	1973
556	3	1974
557	3	1975
558	3	1976
559	3	1977
560	3	1978
561	3	1979
562	3	1980
563	3	1981
564	3	1982
565	3	1983
566	3	1984
567	3	1985
568	3	1986
569	3	1987
570	3	1988
571	3	1989
572	3	1990
573	3	1991
574	3	1992
575	3	1993
576	3	1994
577	3	1995
578	3	1996
579	3	1997
580	3	1998
\.


--
-- TOC entry 2342 (class 0 OID 0)
-- Dependencies: 204
-- Name: sesn_sesn__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('sesn_sesn__id_seq', 580, true);


--
-- TOC entry 2245 (class 0 OID 104094)
-- Dependencies: 205
-- Data for Name: sstm; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY sstm (sstm__id, sstmdscr, sstnmbr) FROM stdin;
\.


--
-- TOC entry 2343 (class 0 OID 0)
-- Dependencies: 206
-- Name: sstm_sstm__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('sstm_sstm__id_seq', 1, false);


--
-- TOC entry 2247 (class 0 OID 104102)
-- Dependencies: 207
-- Data for Name: tpac; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tpac (tpac__id, tpacdscr) FROM stdin;
1	Menú
2	Proceso
\.


--
-- TOC entry 2344 (class 0 OID 0)
-- Dependencies: 208
-- Name: tpac_tpac__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tpac_tpac__id_seq', 2, true);


--
-- TOC entry 2249 (class 0 OID 104107)
-- Dependencies: 209
-- Data for Name: tpdc; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tpdc (tpdc__id, tpdccdgo, tpdcdscr) FROM stdin;
1	MEM	MEMORANDO
2	OFI	OFICIO
3	SUM	SUMILLA
4	CIR	CIRCULAR
5	DEX	DOCUMENTO EXTERNO
\.


--
-- TOC entry 2345 (class 0 OID 0)
-- Dependencies: 210
-- Name: tpdc_tpdc__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tpdc_tpdc__id_seq', 5, true);


--
-- TOC entry 2251 (class 0 OID 104112)
-- Dependencies: 211
-- Data for Name: tpdp; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tpdp (tpdp__id, tpdpcdgo, tpdpdscr) FROM stdin;
1	DIR	DIRECCIÓN
2	GES	GESTIÓN
\.


--
-- TOC entry 2346 (class 0 OID 0)
-- Dependencies: 212
-- Name: tpdp_tpdp__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tpdp_tpdp__id_seq', 2, true);


--
-- TOC entry 2253 (class 0 OID 104117)
-- Dependencies: 213
-- Data for Name: tppd; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tppd (tppd__id, tppdcdgo, tppddscr, tppdtmpo) FROM stdin;
1	URG	URGENTE	4
2	ALTA	ALTA	24
3	MED	MEDIA	48
4	BAJ	BAJA	72
\.


--
-- TOC entry 2347 (class 0 OID 0)
-- Dependencies: 214
-- Name: tppd_tppd__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tppd_tppd__id_seq', 4, true);


--
-- TOC entry 2255 (class 0 OID 104122)
-- Dependencies: 215
-- Data for Name: tppr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tppr (tppr__id, tpprcdgo, tpprdscr) FROM stdin;
1	N	NATURAL
2	J	JURIDICA
\.


--
-- TOC entry 2348 (class 0 OID 0)
-- Dependencies: 216
-- Name: tppr_tppr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tppr_tppr__id_seq', 2, true);


--
-- TOC entry 2257 (class 0 OID 104127)
-- Dependencies: 217
-- Data for Name: tptr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY tptr (tptr__id, tptrcdgo, tptrdscr) FROM stdin;
1	C	CONFIDENCIAL
2	N	NORMAL
\.


--
-- TOC entry 2349 (class 0 OID 0)
-- Dependencies: 218
-- Name: tptr_tptr__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tptr_tptr__id_seq', 2, true);


--
-- TOC entry 2259 (class 0 OID 104132)
-- Dependencies: 219
-- Data for Name: trmt; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY trmt (trmt__id, anio__id, trmtpdre, tpdc__id, prsn__de, tppr__id, edtr__id, tptr__id, orgn__id, trmtcdgo, trmtasnt, trmtanxo, trmttxto, trmtampz, trmtextr, trmtnota, trmtetdo, tppd__id, trmtobsr, trmtnmro, trmtfccr, trmtfcen, trmtfcmd, trmtfcrv, dpto__de) FROM stdin;
\.


--
-- TOC entry 2350 (class 0 OID 0)
-- Dependencies: 220
-- Name: trmt_trmt__id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trmt_trmt__id_seq', 30, true);


--
-- TOC entry 2003 (class 2606 OID 104171)
-- Name: audt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audt
    ADD CONSTRAINT audt_pkey PRIMARY KEY (audt__id);


--
-- TOC entry 2009 (class 2606 OID 104173)
-- Name: ddlb_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ddlb
    ADD CONSTRAINT ddlb_pkey PRIMARY KEY (ddlb__id);


--
-- TOC entry 2015 (class 2606 OID 104175)
-- Name: logf_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY logf
    ADD CONSTRAINT logf_pkey PRIMARY KEY (logf__id);


--
-- TOC entry 1997 (class 2606 OID 104177)
-- Name: pk_accn; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY accn
    ADD CONSTRAINT pk_accn PRIMARY KEY (accn__id);


--
-- TOC entry 1999 (class 2606 OID 104179)
-- Name: pk_accs; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY accs
    ADD CONSTRAINT pk_accs PRIMARY KEY (accs__id);


--
-- TOC entry 2001 (class 2606 OID 104181)
-- Name: pk_anio; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY anio
    ADD CONSTRAINT pk_anio PRIMARY KEY (anio__id);


--
-- TOC entry 2005 (class 2606 OID 104183)
-- Name: pk_ctrl; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ctrl
    ADD CONSTRAINT pk_ctrl PRIMARY KEY (ctrl__id);


--
-- TOC entry 2007 (class 2606 OID 104185)
-- Name: pk_dctr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dctr
    ADD CONSTRAINT pk_dctr PRIMARY KEY (dctr__id);


--
-- TOC entry 2011 (class 2606 OID 104187)
-- Name: pk_dpto; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dpto
    ADD CONSTRAINT pk_dpto PRIMARY KEY (dpto__id);


--
-- TOC entry 2013 (class 2606 OID 104189)
-- Name: pk_edtr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY edtr
    ADD CONSTRAINT pk_edtr PRIMARY KEY (edtr__id);


--
-- TOC entry 2017 (class 2606 OID 104191)
-- Name: pk_mdlo; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mdlo
    ADD CONSTRAINT pk_mdlo PRIMARY KEY (mdlo__id);


--
-- TOC entry 2019 (class 2606 OID 104193)
-- Name: pk_nmro; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY nmro
    ADD CONSTRAINT pk_nmro PRIMARY KEY (nmro__id);


--
-- TOC entry 2021 (class 2606 OID 104195)
-- Name: pk_obtr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY obtr
    ADD CONSTRAINT pk_obtr PRIMARY KEY (obtr__id);


--
-- TOC entry 2023 (class 2606 OID 104197)
-- Name: pk_orgn; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY orgn
    ADD CONSTRAINT pk_orgn PRIMARY KEY (orgn__id);


--
-- TOC entry 2025 (class 2606 OID 104199)
-- Name: pk_perm; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY perm
    ADD CONSTRAINT pk_perm PRIMARY KEY (perm__id);


--
-- TOC entry 2027 (class 2606 OID 104201)
-- Name: pk_prfl; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prfl
    ADD CONSTRAINT pk_prfl PRIMARY KEY (prfl__id);


--
-- TOC entry 2029 (class 2606 OID 104203)
-- Name: pk_prms; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prms
    ADD CONSTRAINT pk_prms PRIMARY KEY (prms__id);


--
-- TOC entry 2031 (class 2606 OID 104205)
-- Name: pk_prsn; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prsn
    ADD CONSTRAINT pk_prsn PRIMARY KEY (prsn__id);


--
-- TOC entry 2035 (class 2606 OID 104207)
-- Name: pk_prtr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prtr
    ADD CONSTRAINT pk_prtr PRIMARY KEY (prtr__id);


--
-- TOC entry 2037 (class 2606 OID 104209)
-- Name: pk_prus; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prus
    ADD CONSTRAINT pk_prus PRIMARY KEY (prus__id);


--
-- TOC entry 2039 (class 2606 OID 104211)
-- Name: pk_rltr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rltr
    ADD CONSTRAINT pk_rltr PRIMARY KEY (rltr__id);


--
-- TOC entry 2041 (class 2606 OID 104213)
-- Name: pk_sesn; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sesn
    ADD CONSTRAINT pk_sesn PRIMARY KEY (sesn__id);


--
-- TOC entry 2045 (class 2606 OID 104215)
-- Name: pk_tpac; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tpac
    ADD CONSTRAINT pk_tpac PRIMARY KEY (tpac__id);


--
-- TOC entry 2047 (class 2606 OID 104217)
-- Name: pk_tpdc; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tpdc
    ADD CONSTRAINT pk_tpdc PRIMARY KEY (tpdc__id);


--
-- TOC entry 2049 (class 2606 OID 104219)
-- Name: pk_tpdp; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tpdp
    ADD CONSTRAINT pk_tpdp PRIMARY KEY (tpdp__id);


--
-- TOC entry 2051 (class 2606 OID 104221)
-- Name: pk_tppd; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tppd
    ADD CONSTRAINT pk_tppd PRIMARY KEY (tppd__id);


--
-- TOC entry 2053 (class 2606 OID 104223)
-- Name: pk_tppr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tppr
    ADD CONSTRAINT pk_tppr PRIMARY KEY (tppr__id);


--
-- TOC entry 2055 (class 2606 OID 104225)
-- Name: pk_tptr; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tptr
    ADD CONSTRAINT pk_tptr PRIMARY KEY (tptr__id);


--
-- TOC entry 2057 (class 2606 OID 104227)
-- Name: pk_trmt; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT pk_trmt PRIMARY KEY (trmt__id);


--
-- TOC entry 2033 (class 2606 OID 104229)
-- Name: prsn_prsncdgo_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY prsn
    ADD CONSTRAINT prsn_prsncdgo_key UNIQUE (prsncdgo);


--
-- TOC entry 2043 (class 2606 OID 104231)
-- Name: sstm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sstm
    ADD CONSTRAINT sstm_pkey PRIMARY KEY (sstm__id);


--
-- TOC entry 2058 (class 2606 OID 104232)
-- Name: accnfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accn
    ADD CONSTRAINT accnfk01 FOREIGN KEY (mdlo__id) REFERENCES mdlo(mdlo__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2059 (class 2606 OID 104237)
-- Name: accnfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accn
    ADD CONSTRAINT accnfk02 FOREIGN KEY (tpac__id) REFERENCES tpac(tpac__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2060 (class 2606 OID 104242)
-- Name: accnfk03; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accn
    ADD CONSTRAINT accnfk03 FOREIGN KEY (ctrl__id) REFERENCES ctrl(ctrl__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2061 (class 2606 OID 104247)
-- Name: accsfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accs
    ADD CONSTRAINT accsfk01 FOREIGN KEY (prsn__id) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2066 (class 2606 OID 104252)
-- Name: dctrfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dctr
    ADD CONSTRAINT dctrfk01 FOREIGN KEY (trmt__id) REFERENCES trmt(trmt__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2067 (class 2606 OID 104257)
-- Name: dctrfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dctr
    ADD CONSTRAINT dctrfk02 FOREIGN KEY (trmtanxo) REFERENCES trmt(trmt__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2068 (class 2606 OID 104262)
-- Name: dptofk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dpto
    ADD CONSTRAINT dptofk01 FOREIGN KEY (tpdp__id) REFERENCES tpdp(tpdp__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2069 (class 2606 OID 104267)
-- Name: dptofk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dpto
    ADD CONSTRAINT dptofk02 FOREIGN KEY (dptopdre) REFERENCES dpto(dpto__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2062 (class 2606 OID 104272)
-- Name: fk2d981284ad016; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accs
    ADD CONSTRAINT fk2d981284ad016 FOREIGN KEY (usro__id) REFERENCES prsn(prsn__id);


--
-- TOC entry 2063 (class 2606 OID 104277)
-- Name: fk2d9812cda6a936; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY accs
    ADD CONSTRAINT fk2d9812cda6a936 FOREIGN KEY (prsnasgn) REFERENCES prsn(prsn__id);


--
-- TOC entry 2064 (class 2606 OID 104282)
-- Name: fk2ddbc464a53277; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audt
    ADD CONSTRAINT fk2ddbc464a53277 FOREIGN KEY (prfl__id) REFERENCES prfl(prfl__id);


--
-- TOC entry 2065 (class 2606 OID 104287)
-- Name: fk2ddbc484ad016; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audt
    ADD CONSTRAINT fk2ddbc484ad016 FOREIGN KEY (usro__id) REFERENCES prsn(prsn__id);


--
-- TOC entry 2070 (class 2606 OID 104292)
-- Name: fk32c5a27a8d793d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY logf
    ADD CONSTRAINT fk32c5a27a8d793d FOREIGN KEY (logfusro) REFERENCES prsn(prsn__id);


--
-- TOC entry 2080 (class 2606 OID 104297)
-- Name: fk34a4001ed2abe3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prtr
    ADD CONSTRAINT fk34a4001ed2abe3 FOREIGN KEY (dpto__id) REFERENCES dpto(dpto__id);


--
-- TOC entry 2084 (class 2606 OID 104302)
-- Name: fk34a420cda6a936; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prus
    ADD CONSTRAINT fk34a420cda6a936 FOREIGN KEY (prsnasgn) REFERENCES prsn(prsn__id);


--
-- TOC entry 2089 (class 2606 OID 104307)
-- Name: fk3674a51ed2ab49; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT fk3674a51ed2ab49 FOREIGN KEY (dpto__de) REFERENCES dpto(dpto__id);


--
-- TOC entry 2090 (class 2606 OID 104312)
-- Name: fk3674a52c20b322; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT fk3674a52c20b322 FOREIGN KEY (tppd__id) REFERENCES tppd(tppd__id);


--
-- TOC entry 2091 (class 2606 OID 104317)
-- Name: fk3674a5d0da9f54; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT fk3674a5d0da9f54 FOREIGN KEY (tppr__id) REFERENCES tppr(tppr__id);


--
-- TOC entry 2071 (class 2606 OID 104322)
-- Name: nmrofk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY nmro
    ADD CONSTRAINT nmrofk01 FOREIGN KEY (tpdc__id) REFERENCES tpdc(tpdc__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2072 (class 2606 OID 104327)
-- Name: nmrofk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY nmro
    ADD CONSTRAINT nmrofk02 FOREIGN KEY (dpto__id) REFERENCES dpto(dpto__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2073 (class 2606 OID 104332)
-- Name: obtrfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY obtr
    ADD CONSTRAINT obtrfk01 FOREIGN KEY (trmt__id) REFERENCES trmt(trmt__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2074 (class 2606 OID 104337)
-- Name: obtrfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY obtr
    ADD CONSTRAINT obtrfk02 FOREIGN KEY (prsn__id) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2075 (class 2606 OID 104342)
-- Name: orgnfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orgn
    ADD CONSTRAINT orgnfk01 FOREIGN KEY (tppr__id) REFERENCES tppr(tppr__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2076 (class 2606 OID 104347)
-- Name: prflfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prfl
    ADD CONSTRAINT prflfk01 FOREIGN KEY (prflpdre) REFERENCES prfl(prfl__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2077 (class 2606 OID 104352)
-- Name: prmsfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prms
    ADD CONSTRAINT prmsfk01 FOREIGN KEY (accn__id) REFERENCES accn(accn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2078 (class 2606 OID 104357)
-- Name: prmsfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prms
    ADD CONSTRAINT prmsfk02 FOREIGN KEY (prfl__id) REFERENCES prfl(prfl__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2079 (class 2606 OID 104362)
-- Name: prsnfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prsn
    ADD CONSTRAINT prsnfk01 FOREIGN KEY (dpto__id) REFERENCES dpto(dpto__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2081 (class 2606 OID 104367)
-- Name: prtrfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prtr
    ADD CONSTRAINT prtrfk01 FOREIGN KEY (rltr__id) REFERENCES rltr(rltr__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2082 (class 2606 OID 104372)
-- Name: prtrfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prtr
    ADD CONSTRAINT prtrfk02 FOREIGN KEY (prsn__id) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2083 (class 2606 OID 104377)
-- Name: prtrfk03; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prtr
    ADD CONSTRAINT prtrfk03 FOREIGN KEY (trmt__id) REFERENCES trmt(trmt__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2085 (class 2606 OID 104382)
-- Name: prusfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prus
    ADD CONSTRAINT prusfk01 FOREIGN KEY (prsn__id) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2086 (class 2606 OID 104387)
-- Name: prusfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY prus
    ADD CONSTRAINT prusfk02 FOREIGN KEY (perm__id) REFERENCES perm(perm__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2087 (class 2606 OID 104392)
-- Name: sesnfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sesn
    ADD CONSTRAINT sesnfk01 FOREIGN KEY (prfl__id) REFERENCES prfl(prfl__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2088 (class 2606 OID 104397)
-- Name: sesnfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sesn
    ADD CONSTRAINT sesnfk02 FOREIGN KEY (prsn__id) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2092 (class 2606 OID 104402)
-- Name: trmtfk01; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk01 FOREIGN KEY (tpdc__id) REFERENCES tpdc(tpdc__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2093 (class 2606 OID 104407)
-- Name: trmtfk02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk02 FOREIGN KEY (prsn__de) REFERENCES prsn(prsn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2094 (class 2606 OID 104412)
-- Name: trmtfk04; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk04 FOREIGN KEY (tppr__id) REFERENCES tppd(tppd__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2095 (class 2606 OID 104417)
-- Name: trmtfk05; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk05 FOREIGN KEY (trmtpdre) REFERENCES trmt(trmt__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2096 (class 2606 OID 104422)
-- Name: trmtfk08; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk08 FOREIGN KEY (anio__id) REFERENCES anio(anio__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2097 (class 2606 OID 104427)
-- Name: trmtfk09; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk09 FOREIGN KEY (edtr__id) REFERENCES edtr(edtr__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2098 (class 2606 OID 104432)
-- Name: trmtfk10; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk10 FOREIGN KEY (tptr__id) REFERENCES tptr(tptr__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2099 (class 2606 OID 104437)
-- Name: trmtfk11; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trmt
    ADD CONSTRAINT trmtfk11 FOREIGN KEY (orgn__id) REFERENCES orgn(orgn__id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 2267 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2014-03-19 15:15:37

--
-- PostgreSQL database dump complete
--

